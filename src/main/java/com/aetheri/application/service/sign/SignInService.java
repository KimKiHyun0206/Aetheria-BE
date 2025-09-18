package com.aetheri.application.service.sign;

import com.aetheri.application.dto.KakaoTokenResponse;
import com.aetheri.application.dto.SignInResponse;
import com.aetheri.application.dto.jwt.RefreshTokenIssueResponse;
import com.aetheri.application.port.in.sign.SignInPort;
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

/**
 * 회원가입 서비스
 */
@Slf4j
@Service
public class SignInService implements SignInPort {
    private final KakaoGetAccessTokenPort kakaoGetAccessTokenPort;
    private final KakaoUserInformationInquiryPort kakaoUserInformationInquiryPort;
    private final RunnerRepositoryPort runnerRepositoryPort;
    private final KakaoTokenRepositoryPort kakaoTokenRepositoryPort;
    private final RedisRefreshTokenRepositoryPort redisRefreshTokenRepositoryPort;
    private final JwtTokenProviderPort jwtTokenProviderPort;
    private final SignUpService signUpService;

    private final long REFRESH_TOKEN_EXPIRATION_DAYS;

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
        this.REFRESH_TOKEN_EXPIRATION_DAYS = jwtProperties.refreshTokenExpirationDays();
    }

    /**
     * 카카오가 발급해준 코드로 로그인을 진행하기 위한 메소드
     *
     * @param code 카카오가 발급한 로그인 코드
     */
    @Override
    public Mono<SignInResponse> signIn(String code) {
                // 코드가 유효한지 검증합니다.
        return validateCode(code)
                // 카카오에서 액세스 토큰을 가져옵니다.
                .flatMap(this::getKakaoToken)
                // 액세스 토큰으로 카카오 API를 사용하여 사용자 정보를 가져옵니다.
                .flatMap(this::getUserInfo)
                // 회원가입을 하거나 로그인을 합니다.
                .flatMap(this::findOrSignUpRunner)
                // 카카오 토큰을 데이터베이스에 저장합니다.
                .flatMap(this::saveKakaoToken)
                // 서버의 액세스 토큰과 리프래쉬 토큰을 발급하고 저장합니다.
                .flatMap(this::issueTokensAndSave)
                // 액세스 토큰과 리프래쉬 토큰을 반환합니다.
                .map(this::toSignInResponse);
    }

    /**
     * 코드가 유효한지 검증합니다.
     *
     * @param code 검증할 코드
     * @implNote 만약 코드가 유효하지 않다면 예외를 발생시킵니다.
     */
    private Mono<String> validateCode(String code) {
        return Mono.fromSupplier(() -> code)
                .filter(c -> c != null && !c.trim().isEmpty())
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.NOT_FOUND_AUTHORIZATION_CODE,
                        "인증 코드를 찾을 수 없습니다."
                )));
    }

    /**
     * 카카오 토큰(액세스/리프래쉬)를 코드로 가져옵니다.
     *
     * @param code 카카오에서 발급한 로그인 코드
     * @implNote 만약 카카오 API에서 가져온 토큰이 비어있다면 예외를 발생시킵니다.
     */
    private Mono<KakaoTokenResponse> getKakaoToken(String code) {
        return kakaoGetAccessTokenPort.tokenRequest(code)
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.NOT_FOUND_ACCESS_TOKEN,
                        "카카오에서 액세스 토큰을 찾을 수 없습니다."
                )));

    }

    /**
     * 카카오 토큰으로 사용자 정보를 조회합니다.
     *
     * @param dto 카카오의 토큰이 담긴 DTO
     * @implNote 만약 사용자 정보를 가져오기 못했다면 예외를 발생시킵니다.
     */
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

    /**
     * 카카오 토큰을 데이터베이스에 저장합니다.
     *
     * @param dto 저장할 카카오 토큰과 사용자의 ID
     */
    private Mono<Long> saveKakaoToken(KakaoTokenAndId dto) {
        return kakaoTokenRepositoryPort.save(
                        dto.id(),
                        dto.accessToken,
                        dto.refreshToken()
                )
                .thenReturn(dto.id());
    }

    /**
     * 사용자를 찾아서 존재하면 로그인하고 없다면 회원가입 시킵니다.
     *
     * @param dto 회원가입 또는 로그인에 필요한 사용자의 ID와 이름
     */
    private Mono<KakaoTokenAndId> findOrSignUpRunner(KakaoTokenAndId dto) {
        return runnerRepositoryPort.existsByKakaoId(dto.id())
                .flatMap(exists -> exists ?
                        runnerRepositoryPort.findByKakaoId(dto.id()) :
                        signUpService.signUp(dto.id(), dto.name())
                                .then(runnerRepositoryPort.findByKakaoId(dto.id()))
                ).map(runner -> new KakaoTokenAndId(
                        dto.accessToken(),
                        dto.refreshToken(),
                        runner.getId(),
                        runner.getName())
                );
    }

    /**
     * 이 서비스 내부에서만 사용하는 카카오 토큰과 사용자 ID를 운반하기 위한 DTO
     */
    private record KakaoTokenAndId(
            // 카카오에서 발급한 액세스 토큰
            String accessToken,
            // 카카오에서 발급한 리프래쉬 토큰
            String refreshToken,
            // 사용자의 카카오 ID / 사용자의 ID
            Long id,
            // 사용자의 카카오 nickname
            String name
    ) {
    }

    /**
     * JWT 토큰을 생성하고 저장하는 메소드
     *
     * @param runnerId 사용자의 ID
     */
    private Mono<TokenBundle> issueTokensAndSave(Long runnerId) {
        Authentication auth = AuthenticationConverter.toAuthentication(runnerId);
        String accessToken = jwtTokenProviderPort.generateAccessToken(auth);
        RefreshTokenIssueResponse refreshToken = jwtTokenProviderPort.generateRefreshToken(auth);

        return redisRefreshTokenRepositoryPort
                .saveRefreshToken(runnerId, refreshToken.refreshToken())
                .thenReturn(new TokenBundle(accessToken, refreshToken.refreshToken()));
    }

    /**
     * 이 서비스 내에서만 사용할 JWT 토큰 번들 운반 DTO
     */
    private record TokenBundle(
            String accessToken,
            String refreshToken
    ) {
    }

    /**
     * 토큰 번들을 응답 DTO로 래핑하기 위한 메소드
     */
    private SignInResponse toSignInResponse(TokenBundle tokenBundle) {
        long expiresInSeconds = 60 * 60 * 24 * REFRESH_TOKEN_EXPIRATION_DAYS;
        return new SignInResponse(tokenBundle.accessToken(), tokenBundle.refreshToken(), expiresInSeconds);
    }
}