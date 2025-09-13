package com.aetheri.interfaces.web.handler;

import com.aetheri.application.service.sign.SignInService;
import com.aetheri.application.service.sign.SignOffService;
import com.aetheri.application.service.sign.SignOutService;
import com.aetheri.application.util.AuthenticationUtils;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.config.properties.JWTProperties;
import com.aetheri.infrastructure.config.properties.KakaoProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class AuthHandler {
    private final SignInService signInService;
    private final SignOffService signOffService;
    private final SignOutService signOutService;

    private final String clientId;
    private final String redirectUri;
    private final String refreshTokenCookie;
    private final String accessTokenHeader;

    public AuthHandler(SignInService signInService, SignOffService signOffService, SignOutService signOutService, KakaoProperties kakaoProperties, JWTProperties jwtProperties) {
        this.signInService = signInService;
        this.signOffService = signOffService;
        this.signOutService = signOutService;
        this.clientId = kakaoProperties.clientId();
        this.redirectUri = kakaoProperties.redirectUri();
        this.refreshTokenCookie = jwtProperties.refreshTokenCookie();
        this.accessTokenHeader = jwtProperties.accessTokenHeader();
    }

    /**
     * 인가 코드 요청 API. * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-code">카카오 REST API</a> * * @implNote 리다이렉트를 걸어주는 것이기 때문에 핸들러에서 처리하도록 함. 만약 리다이랙트 URI 생성을 분리한다면 분리하여도 됨.
     */
    public Mono<ServerResponse> redirectToKakaoLogin(ServerRequest request) {
        String kakaoAuthUrl = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .toUriString();

        return ServerResponse.temporaryRedirect(URI.create(kakaoAuthUrl)).build();
    }

    /**
     * 로그인 후 사용자 정보를 가져오게 하는 핸들러
     */
    public Mono<ServerResponse> getKakaoAccessToken(ServerRequest request) {

        return findCodeFromUrl(request)
                .flatMap(signInService::login)
                .flatMap(response -> {
                    log.info("[AuthHandler] 로그인 성공: \naccessToken={} \n refreshToken={}", response.accessToken(), response.refreshToken());

                    ResponseCookie cookie = ResponseCookie
                            .from(refreshTokenCookie, response.refreshToken())
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .sameSite("Strict")
                            .maxAge(response.refreshTokenExpirationTime())
                            .build();

                    return ServerResponse.ok()
                            .header(accessTokenHeader, response.accessToken())
                            .cookie(cookie)
                            .body(Mono.empty(), Void.class);
                });
    }

    public Mono<ServerResponse> signOff(ServerRequest request) {
        return AuthenticationUtils.extractRunnerIdFromRequest(request)
                .flatMap(runnerId -> {
                    log.info("[AuthHandler] signOff: runnerId={}", runnerId);
                    return signOffService.signOff(runnerId).then(ServerResponse.noContent().build());
                });
    }

    public Mono<ServerResponse> signOut(ServerRequest request) {
        return AuthenticationUtils.extractRunnerIdFromRequest(request)
                .flatMap(runnerId -> {
                    log.info("[AuthHandler] signOut: runnerId={}", runnerId);
                    return signOutService.signOut(runnerId).then(ServerResponse.noContent().build());
                });
    }


    private Mono<String> findCodeFromUrl(ServerRequest request) {
        return Mono.just(request.queryParam("code")
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorMessage.NOT_FOUND_AUTHORIZATION_CODE,
                                "인증 코드를 응답에서 찾을 수 없습니다."
                        )
                ));
    }
}