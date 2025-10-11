package com.aetheri.application.service.sign;

import com.aetheri.application.dto.KakaoTokenResponse;
import com.aetheri.application.dto.SignInResponse;
import com.aetheri.application.dto.jwt.RefreshTokenIssueResponse;
import com.aetheri.application.port.in.sign.SignInUseCase;
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
 * 카카오 인증 코드를 기반으로 로그인(Sign-In) 및 자동 회원가입을 처리하는 유즈케이스({@link SignInUseCase})의 구현체입니다.
 * 이 서비스는 외부 카카오 API 통신, 내부 데이터베이스 및 Redis 저장소 접근, JWT 발급 등 로그인에 필요한 전 과정을 조정합니다.
 */
@Slf4j
@Service
public class SignInPort implements SignInUseCase {
    private final KakaoGetAccessTokenPort kakaoGetAccessTokenPort;
    private final KakaoUserInformationInquiryPort kakaoUserInformationInquiryPort;
    private final RunnerRepositoryPort runnerRepositoryPort;
    private final KakaoTokenRepositoryPort kakaoTokenRepositoryPort;
    private final RedisRefreshTokenRepositoryPort redisRefreshTokenRepositoryPort;
    private final JwtTokenProviderPort jwtTokenProviderPort;
    private final SignUpPort signUpPort;

    private final long REFRESH_TOKEN_EXPIRATION_DAYS;

    /**
     * {@code SignInPort}의 생성자입니다. 필요한 모든 의존성을 주입받고 JWT 설정을 초기화합니다.
     */
    public SignInPort(
            KakaoGetAccessTokenPort kakaoGetAccessTokenPort,
            KakaoUserInformationInquiryPort kakaoUserInformationInquiryPort,
            RunnerRepositoryPort runnerRepositoryPort,
            KakaoTokenRepositoryPort kakaoTokenRepositoryPort, RedisRefreshTokenRepositoryPort redisRefreshTokenRepositoryPort,
            JwtTokenProviderPort jwtTokenProviderPort,
            SignUpPort signUpPort,
            JWTProperties jwtProperties
    ) {
        this.kakaoGetAccessTokenPort = kakaoGetAccessTokenPort;
        this.kakaoUserInformationInquiryPort = kakaoUserInformationInquiryPort;
        this.runnerRepositoryPort = runnerRepositoryPort;
        this.kakaoTokenRepositoryPort = kakaoTokenRepositoryPort;
        this.redisRefreshTokenRepositoryPort = redisRefreshTokenRepositoryPort;
        this.jwtTokenProviderPort = jwtTokenProviderPort;
        this.signUpPort = signUpPort;
        this.REFRESH_TOKEN_EXPIRATION_DAYS = jwtProperties.refreshTokenExpirationDays();
    }

    /**
     * 카카오 인증 서버가 발급한 인증 코드({@code code})를 사용하여 로그인 절차를 진행합니다.
     *
     * <p>전체 로그인 절차는 다음과 같습니다:</p>
     * <ol>
     * <li>인증 코드 유효성 검증</li>
     * <li>카카오 API로부터 액세스 토큰 획득</li>
     * <li>카카오 액세스 토큰으로 사용자 정보 조회</li>
     * <li>조회된 정보로 기존 사용자 확인 또는 신규 회원가입 처리</li>
     * <li>카카오 토큰 정보 데이터베이스 저장</li>
     * <li>시스템 JWT 토큰(액세스/리프레시) 발급 및 저장</li>
     * <li>최종 응답 DTO 반환</li>
     * </ol>
     *
     * @param code 카카오가 로그인 성공 후 리다이렉션 시 발급한 인증 코드 문자열입니다.
     * @return 로그인에 성공하면 시스템 JWT 토큰 정보를 담은 {@code SignInResponse}를 발행하는 {@code Mono}입니다.
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
                // 서버의 액세스 토큰과 리프레시 토큰을 발급하고 저장합니다.
                .flatMap(this::issueTokensAndSave)
                // 액세스 토큰과 리프레시 토큰을 반환합니다.
                .map(this::toSignInResponse);
    }

    /**
     * 인증 코드({@code code})의 null 또는 공백 여부를 검증합니다.
     *
     * @param code 검증할 인증 코드입니다.
     * @return 코드가 유효하면 코드를 발행하는 {@code Mono<String>}입니다.
     * @throws BusinessException 코드가 유효하지 않다면(null/공백) {@code NOT_FOUND_AUTHORIZATION_CODE} 예외를 발생시킵니다.
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
     * 카카오 인증 서버에 액세스 토큰 발급을 요청합니다.
     *
     * @param code 카카오에서 발급한 로그인 인증 코드입니다.
     * @return 카카오의 토큰 응답({@code KakaoTokenResponse})을 발행하는 {@code Mono}입니다.
     * @throws BusinessException 카카오 API에서 토큰을 가져오지 못했다면 {@code NOT_FOUND_ACCESS_TOKEN} 예외를 발생시킵니다.
     */
    private Mono<KakaoTokenResponse> getKakaoToken(String code) {
        return kakaoGetAccessTokenPort.tokenRequest(code)
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.NOT_FOUND_ACCESS_TOKEN,
                        "카카오에서 액세스 토큰을 찾을 수 없습니다."
                )));

    }

    /**
     * 카카오 액세스 토큰을 사용하여 사용자 정보를 조회하고, 내부 DTO({@code KakaoTokenAndId})로 변환합니다.
     *
     * @param dto 카카오의 토큰이 담긴 DTO입니다.
     * @return 카카오 토큰 정보와 사용자 ID/이름을 담은 {@code KakaoTokenAndId}를 발행하는 {@code Mono}입니다.
     * @throws BusinessException 사용자 정보를 조회하지 못했다면 {@code NOT_FOUND_RUNNER} 예외를 발생시킵니다.
     */
    private Mono<KakaoTokenAndId> getUserInfo(KakaoTokenResponse dto) {
        return kakaoUserInformationInquiryPort.userInformationInquiry(dto.accessToken())
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.NOT_FOUND_RUNNER,
                        "카카오에서 사용자 정보를 찾을 수 없습니다."
                )))
                .map(userInfo -> {
                    // 사용자 닉네임 추출 로직: properties -> kakaoAccount.profile 순서로 닉네임 시도, 없으면 기본값 사용
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
     * 카카오 사용자 ID를 기반으로 데이터베이스에서 사용자를 찾습니다.
     * 사용자가 존재하지 않으면 자동 회원가입을 수행하고, 최종적으로 내부 DTO를 갱신하여 반환합니다.
     *
     * @param dto 카카오 토큰 정보와 사용자 ID, 이름이 담긴 DTO입니다.
     * @return 시스템 ID로 갱신된 {@code KakaoTokenAndId}를 발행하는 {@code Mono}입니다.
     */
    private Mono<KakaoTokenAndId> findOrSignUpRunner(KakaoTokenAndId dto) {
        return runnerRepositoryPort.existsByKakaoId(dto.id())
                .flatMap(exists -> exists ?
                        // 1. 사용자 존재 시: 사용자 정보 조회 (로그인)
                        runnerRepositoryPort.findByKakaoId(dto.id()) :
                        // 2. 사용자 미존재 시: 회원가입 수행 후 사용자 정보 조회
                        signUpPort.signUp(dto.id(), dto.name())
                                .then(runnerRepositoryPort.findByKakaoId(dto.id()))
                ).map(runner -> new KakaoTokenAndId(
                        dto.accessToken(),
                        dto.refreshToken(),
                        runner.getId(), // 시스템 DB의 runner ID로 갱신
                        runner.getName())
                );
    }

    /**
     * 카카오 토큰 정보(액세스/리프레시)를 데이터베이스에 저장하거나 갱신합니다.
     *
     * @param dto 카카오 토큰과 사용자 ID가 담긴 DTO입니다.
     * @return 저장/갱신에 성공하면 해당 사용자의 시스템 ID를 발행하는 {@code Mono<Long>}입니다.
     */
    private Mono<Long> saveKakaoToken(KakaoTokenAndId dto) {
        return kakaoTokenRepositoryPort.save(
                        dto.id(),
                        dto.accessToken(),
                        dto.refreshToken()
                )
                .thenReturn(dto.id());
    }

    /**
     * 이 서비스 내부에서만 사용하는 카카오 토큰과 사용자 ID를 운반하기 위한 DTO
     */
    private record KakaoTokenAndId(
            // 카카오에서 발급한 액세스 토큰
            String accessToken,
            // 카카오에서 발급한 리프레시 토큰
            String refreshToken,
            // 사용자의 시스템 ID (로그인 후 갱신됨)
            Long id,
            // 사용자의 닉네임
            String name
    ) {
    }

    /**
     * 새로운 시스템 JWT 토큰(액세스/리프레시)을 생성하고, 리프레시 토큰을 Redis에 저장합니다.
     *
     * @param runnerId JWT 토큰 생성 및 저장에 사용될 사용자의 시스템 ID입니다.
     * @return 생성된 토큰 쌍을 담은 {@code TokenBundle}을 발행하는 {@code Mono}입니다.
     */
    private Mono<TokenBundle> issueTokensAndSave(Long runnerId) {
        // 사용자 ID로 Authentication 객체 생성
        Authentication auth = AuthenticationConverter.toAuthentication(runnerId);
        // 액세스 토큰 생성
        String accessToken = jwtTokenProviderPort.generateAccessToken(auth);
        // 리프레시 토큰 생성
        RefreshTokenIssueResponse refreshToken = jwtTokenProviderPort.generateRefreshToken(auth);

        return redisRefreshTokenRepositoryPort
                // 리프레시 토큰을 Redis에 저장합니다.
                .saveRefreshToken(runnerId, refreshToken.refreshToken())
                // 저장 성공 후, 토큰 번들 DTO를 발행합니다.
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
     * JWT 토큰 번들을 최종 응답 DTO({@code SignInResponse})로 변환합니다.
     *
     * @param tokenBundle JWT 토큰 쌍이 담긴 내부 DTO입니다.
     * @return 최종 로그인 응답 DTO입니다.
     */
    private SignInResponse toSignInResponse(TokenBundle tokenBundle) {
        long expiresInSeconds = 60 * 60 * 24 * REFRESH_TOKEN_EXPIRATION_DAYS;
        return new SignInResponse(tokenBundle.accessToken(), tokenBundle.refreshToken(), expiresInSeconds);
    }
}