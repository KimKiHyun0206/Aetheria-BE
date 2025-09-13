package com.aetheri.application.service.sign;

import com.aetheri.application.dto.KakaoTokenResponse;
import com.aetheri.application.dto.SignInResponse;
import com.aetheri.application.dto.jwt.RefreshTokenIssueResponse;
import com.aetheri.application.port.out.jwt.JwtTokenProviderPort;
import com.aetheri.application.port.out.kakao.KakaoGetAccessTokenPort;
import com.aetheri.application.port.out.kakao.KakaoUserInformationInquiryPort;
import com.aetheri.application.port.out.r2dbc.KakaoTokenRepositoryPort;
import com.aetheri.application.port.out.r2dbc.RunnerRepositoryPort;
import com.aetheri.application.port.out.redis.RedisRefreshTokenRepositoryPort;
import com.aetheri.application.service.converter.AuthenticationConverter;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.config.properties.JWTProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class SignInService {
    private final KakaoGetAccessTokenPort kakaoGetAccessTokenPort;
    private final KakaoUserInformationInquiryPort kakaoUserInformationInquiryPort;
    private final RunnerRepositoryPort runnerRepositoryPort;
    private final KakaoTokenRepositoryPort kakaoTokenRepositoryPort;
    private final RedisRefreshTokenRepositoryPort redisRefreshTokenRepositoryPort;
    private final JwtTokenProviderPort jwtTokenProviderPort;
    private final SignUpService signUpService;

    private final long refreshTokenExpirationDays;

    public SignInService(
            KakaoGetAccessTokenPort kakaoGetAccessTokenPort,
            KakaoUserInformationInquiryPort kakaoUserInformationInquiryPort,
            RunnerRepositoryPort runnerRepositoryPort,
            KakaoTokenRepositoryPort kakaoTokenRepositoryPort, RedisRefreshTokenRepositoryPort redisRefreshTokenRepositoryPort,
            JwtTokenProviderPort jwtTokenProviderPort,
            SignUpService signUpService,
            JWTProperties jwtProperties
    ) {
        this.kakaoGetAccessTokenPort = kakaoGetAccessTokenPort;
        this.kakaoUserInformationInquiryPort = kakaoUserInformationInquiryPort;
        this.runnerRepositoryPort = runnerRepositoryPort;
        this.kakaoTokenRepositoryPort = kakaoTokenRepositoryPort;
        this.redisRefreshTokenRepositoryPort = redisRefreshTokenRepositoryPort;
        this.jwtTokenProviderPort = jwtTokenProviderPort;
        this.signUpService = signUpService;
        this.refreshTokenExpirationDays = jwtProperties.refreshTokenExpirationDays();
    }

    public Mono<SignInResponse> login(String code) {
        return validateCode(code)
                .flatMap(this::getKakaoToken)
                .flatMap(this::getUserInfo)
                .flatMap(this::findOrSignUpRunner)
                .flatMap(this::saveKakaoToken)
                .flatMap(this::issueTokensAndSave)
                .map(this::toSignInResponse);
    }

    private Mono<String> validateCode(String code) {
        return Mono.fromSupplier(() -> code)
                .filter(c -> c != null && !c.trim().isEmpty())
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.NOT_FOUND_AUTHORIZATION_CODE,
                        "인증 코드를 찾을 수 없습니다."
                )));
    }

    private Mono<KakaoTokenResponse> getKakaoToken(String code) {
        return kakaoGetAccessTokenPort.tokenRequest(code)
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.NOT_FOUND_ACCESS_TOKEN,
                        "카카오에서 액세스 토큰을 찾을 수 없습니다."
                )));

    }

    private Mono<KakaoTokenAndId> getUserInfo(KakaoTokenResponse dto) {
        return kakaoUserInformationInquiryPort.userInformationInquiry(dto.accessToken())
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.NOT_FOUND_RUNNER,
                        "카카오에서 사용자 정보를 찾을 수 없습니다."
                )))
                .map(userInfo -> {
                    String name = java.util.Optional.ofNullable(userInfo.properties())
                            .map(p -> Optional.ofNullable(p.get("nickname")))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(s -> !s.isBlank())
                            .orElseGet(() -> {
                                var acc = userInfo.kakaoAccount();
                                var profile = (acc != null) ? acc.profile() : null;
                                var nick = (profile != null) ? profile.nickName() : null;
                                return (nick != null && !nick.isBlank()) ? nick : ("runner-" + userInfo.id());
                            });
                    return new KakaoTokenAndId(dto.accessToken(), dto.refreshToken(), userInfo.id(), name);
                });
    }

    private Mono<Long> saveKakaoToken(KakaoTokenAndId dto) {
        return kakaoTokenRepositoryPort.save(dto.id(), dto.accessToken, dto.refreshToken())
                .thenReturn(dto.id());
    }

    private Mono<KakaoTokenAndId> findOrSignUpRunner(KakaoTokenAndId dto) {
        return runnerRepositoryPort.existsByKakaoId(dto.id())
                .flatMap(exists -> exists
                        ? runnerRepositoryPort.findByKakaoId(dto.id())
                        : signUpService.signUp(dto.id(), dto.name()).then(runnerRepositoryPort.findByKakaoId(dto.id()))
                ).map(runner -> new KakaoTokenAndId(dto.accessToken(), dto.refreshToken(), runner.getId(), runner.getName()));
    }

    private record KakaoTokenAndId(
            String accessToken,
            String refreshToken,
            Long id,
            String name
    ) {
    }

    private Mono<TokenBundle> issueTokensAndSave(Long runner) {
        Authentication auth = AuthenticationConverter.toAuthentication(runner);
        String accessToken = jwtTokenProviderPort.generateAccessToken(auth);
        RefreshTokenIssueResponse refreshToken = jwtTokenProviderPort.generateRefreshToken(auth);

        return redisRefreshTokenRepositoryPort
                .saveRefreshToken(runner, refreshToken.refreshToken())
                .thenReturn(new TokenBundle(accessToken, refreshToken.refreshToken()));
    }

    private record TokenBundle(
            String accessToken,
            String refreshToken
    ) {
    }

    private SignInResponse toSignInResponse(TokenBundle tokenBundle) {
        long expiresInSeconds = 60 * 60 * 24 * refreshTokenExpirationDays;
        return new SignInResponse(tokenBundle.accessToken(), tokenBundle.refreshToken(), expiresInSeconds);
    }
}