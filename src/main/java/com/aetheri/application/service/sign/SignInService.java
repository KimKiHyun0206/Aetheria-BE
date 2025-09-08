package com.aetheri.application.service.sign;

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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SignInService {
    private final KakaoGetAccessTokenPort kakaoGetAccessTokenPort;
    private final KakaoUserInformationInquiryPort kakaoUserInformationInquiryPort;
    private final RunnerRepositoryPort runnerRepositoryPort;
    private final JwtTokenProviderPort jwtTokenProviderPort;
    private final SignUpService signUpService;

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
        this.refreshTokenExpirationDays = jwtProperties.refreshTokenExpirationDays();
    }

    public Mono<SignInResponse> login(String code) {
        ValidationUtils.validateNotEmpty(
                code,
                ErrorMessage.NOT_FOUND_AUTHORIZATION_CODE,
                "인증 코드를 찾을 수 없습니다."
        );

        return kakaoGetAccessTokenPort
                // 액세스 토큰 가져오기
                .tokenRequest(code)
                // 가져온 액세스 토큰으로 사용자 정보 조회하기
                .flatMap(token -> kakaoUserInformationInquiryPort.userInformationInquiry(token.accessToken()))
                // 만약 사용자 정보가 없다면 예외 발생
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.NOT_FOUND_RUNNER,
                        "카카오에서 사용자 정보를 찾을 수 없습니다."
                )))
                // 가져온 사용자 정보로 회원가입 및 로그인 진행
                .flatMap(userInfo ->
                        runnerRepositoryPort.existByKakaoId(userInfo.id())
                                .flatMap(exists -> {
                                    // 1. 만약 존재하지 않는다면 회원가입 Mono를 반환
                                    if (!exists) {
                                        return signUpService.signUp(userInfo)
                                                .then(runnerRepositoryPort.findByKakaoId(userInfo.id()));
                                    }
                                    // 2. 존재한다면 바로 사용자 정보를 찾아서 반환
                                    return runnerRepositoryPort.findByKakaoId(userInfo.id());
                                })
                )
                .flatMap(runner -> {
                    Authentication auth = RunnerAuthenticationConverter.toAuthentication(runner);
                    String accessToken = jwtTokenProviderPort.generateAccessToken(auth);
                    RefreshTokenIssueResponse refreshToken = jwtTokenProviderPort.generateRefreshToken(auth);

                    return Mono.just(new SignInResponse(accessToken, refreshToken.refreshToken(), 60*60*24*refreshTokenExpirationDays));
                });
    }

}