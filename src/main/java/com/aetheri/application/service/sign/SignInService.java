package com.aetheri.application.service.sign;

import com.aetheri.application.dto.SignInResponse;
import com.aetheri.application.dto.jwt.RefreshTokenIssueResponse;
import com.aetheri.application.port.out.jwt.JwtTokenProviderPort;
import com.aetheri.application.port.out.kakao.KakaoGetAccessTokenPort;
import com.aetheri.application.port.out.kakao.KakaoUserInformationInquiryPort;
import com.aetheri.application.port.out.r2dbc.KakaoTokenRepositortyPort;
import com.aetheri.application.port.out.r2dbc.RunnerRepositoryPort;
import com.aetheri.application.service.converter.AuthenticationConverter;
import com.aetheri.application.util.ValidationUtils;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.config.properties.JWTProperties;
import com.aetheri.interfaces.dto.kakao.KakaoTokenResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SignInService {
    private final KakaoGetAccessTokenPort kakaoGetAccessTokenPort;
    private final KakaoUserInformationInquiryPort kakaoUserInformationInquiryPort;
    private final RunnerRepositoryPort runnerRepositoryPort;
    private final KakaoTokenRepositortyPort kakaoTokenRepositortyPort;
    private final JwtTokenProviderPort jwtTokenProviderPort;
    private final SignUpService signUpService;

    private final long refreshTokenExpirationDays;

    public SignInService(
            KakaoGetAccessTokenPort kakaoGetAccessTokenPort,
            KakaoUserInformationInquiryPort kakaoUserInformationInquiryPort,
            RunnerRepositoryPort runnerRepositoryPort,
            KakaoTokenRepositortyPort kakaoTokenRepositortyPort,
            JwtTokenProviderPort jwtTokenProviderPort,
            SignUpService signUpService,
            JWTProperties jwtProperties
    ) {
        this.kakaoGetAccessTokenPort = kakaoGetAccessTokenPort;
        this.kakaoUserInformationInquiryPort = kakaoUserInformationInquiryPort;
        this.runnerRepositoryPort = runnerRepositoryPort;
        this.kakaoTokenRepositortyPort = kakaoTokenRepositortyPort;
        this.jwtTokenProviderPort = jwtTokenProviderPort;
        this.signUpService = signUpService;
        this.refreshTokenExpirationDays = jwtProperties.refreshTokenExpirationDays();
    }

    public Mono<SignInResponse> login(String code) {
        validateCode(code);

        return kakaoGetAccessTokenPort
                .tokenRequest(code)
                .flatMap(this::getUserInfo)
                .flatMap(this::findOrSignUpRunner)
                .flatMap(this::saveKakaoToken)
                .flatMap(this::issueTokens)
                .map(this::toSignInResponse);
    }

    private void validateCode(String code) {
        ValidationUtils.validateNotEmpty(
                code,
                ErrorMessage.NOT_FOUND_AUTHORIZATION_CODE,
                "인증 코드를 찾을 수 없습니다."
        );
    }

    private Mono<KakaoTokenAndId> getUserInfo(KakaoTokenResponseDto dto) {
        return kakaoUserInformationInquiryPort.userInformationInquiry(dto.accessToken())
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.NOT_FOUND_RUNNER,
                        "카카오에서 사용자 정보를 찾을 수 없습니다."
                )))
                .map(userInfo -> new KakaoTokenAndId(dto.accessToken(), dto.refreshToken(), userInfo.id(), userInfo.properties().get("nickname")));
    }

    private Mono<Long> saveKakaoToken(KakaoTokenAndId dto) {
        return kakaoTokenRepositortyPort.save(dto.id(), dto.accessToken, dto.refreshToken())
                .thenReturn(dto.id());
    }

    private Mono<KakaoTokenAndId> findOrSignUpRunner(KakaoTokenAndId dto) {
        return runnerRepositoryPort.existByKakaoId(dto.id())
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

    private Mono<TokenBundle> issueTokens(Long runner) {
        Authentication auth = AuthenticationConverter.toAuthentication(runner);
        String accessToken = jwtTokenProviderPort.generateAccessToken(auth);
        RefreshTokenIssueResponse refreshToken = jwtTokenProviderPort.generateRefreshToken(auth);

        return Mono.just(new TokenBundle(accessToken, refreshToken.refreshToken()));
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