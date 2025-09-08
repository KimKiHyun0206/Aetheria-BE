package com.aetheri.application.service;

import com.aetheri.application.dto.SignInResponse;
import com.aetheri.application.dto.jwt.RefreshTokenIssueResponse;
import com.aetheri.application.port.out.jwt.JwtTokenProviderPort;
import com.aetheri.application.port.out.kakao.KakaoGetAccessTokenPort;
import com.aetheri.application.port.out.kakao.KakaoUserInformationInquiryPort;
import com.aetheri.application.port.out.r2dbc.RunnerRepositoryPort;
import com.aetheri.application.service.converter.RunnerAuthenticationConverter;
import com.aetheri.application.util.ValidationUtils;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.config.properties.JWTProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SignInService {
    private final KakaoGetAccessTokenPort kakaoGetAccessTokenPort;
    private final KakaoUserInformationInquiryPort kakaoUserInformationInquiryPort;
    private final RunnerRepositoryPort runnerRepositoryPort;
    private final JwtTokenProviderPort jwtTokenProviderPort;

    private final SignUpService signUpService;
    private final String accessTokenHeader;
    private final String refreshTokenCookie;
    private final long refreshTokenExpirationDays;

    public SignInService(
            KakaoGetAccessTokenPort kakaoGetAccessTokenPort,
            KakaoUserInformationInquiryPort kakaoUserInformationInquiryPort,
            RunnerRepositoryPort runnerRepositoryPort,
            JwtTokenProviderPort jwtTokenProviderPort,
            SignUpService signUpService,
            JWTProperties jwtProperties
    ) {
        this.kakaoGetAccessTokenPort = kakaoGetAccessTokenPort;
        this.kakaoUserInformationInquiryPort = kakaoUserInformationInquiryPort;
        this.runnerRepositoryPort = runnerRepositoryPort;
        this.jwtTokenProviderPort = jwtTokenProviderPort;
        this.signUpService = signUpService;
        this.accessTokenHeader = jwtProperties.accessTokenHeader();
        this.refreshTokenCookie = jwtProperties.refreshTokenCookie();
        this.refreshTokenExpirationDays = jwtProperties.refreshTokenExpirationDays();
    }

    public Mono<SignInResponse> login(String code) {
        ValidationUtils.validateNotEmpty(
                code,
                ErrorMessage.NOT_FOUND_AUTHORIZATION_CODE,
                "인증 코드를 찾을 수 없습니다."
        );

        return kakaoGetAccessTokenPort
                // 카카오 API에게서 로그인 코드로 액세스 토큰을 받아옴
                .tokenRequest(code)
                // 받아온 토큰으로 사용자 정보를 카카오 API로 조회
                .flatMap(token -> kakaoUserInformationInquiryPort.userInformationInquiry(token.accessToken()))
                // 만약 사용자 조회에 실패했다면 예외 발생
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.NOT_FOUND_RUNNER,
                        "사용자 정보를 찾을 수 없습니다."
                )))
                // 사용자 조회에 성공했다면 회원가입 되어있는지 확인하고 회원가입이 되어있지 않다면 회원가입하고 되어있다면 사용자를 찾아서 반환
                .flatMap(userInfo -> runnerRepositoryPort.existByKakaoId(userInfo.id())
                        .flatMap(exists -> {
                            Mono<Void> signUpMono = exists
                                    ? Mono.empty()
                                    : signUpService.signUp(userInfo).then();
                            return signUpMono.then(runnerRepositoryPort.findByKakaoId(userInfo.id()));
                        })
                )
                // 찾아온 사용자를 사용하여 액세스 토큰과 리프레쉬 토큰 발급
                .flatMap(runner -> {
                    Authentication auth = RunnerAuthenticationConverter.toAuthentication(runner);
                    String accessToken = jwtTokenProviderPort.generateAccessToken(auth);
                    RefreshTokenIssueResponse refreshToken = jwtTokenProviderPort.generateRefreshToken(auth);

                    return Mono.just(new SignInResponse(accessToken, refreshToken.refreshToken(), 60*60*24*refreshTokenExpirationDays));
                });
    }

}