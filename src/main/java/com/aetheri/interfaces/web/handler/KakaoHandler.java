package com.aetheri.interfaces.web.handler;

import com.aetheri.application.service.SignInService;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.config.properties.JWTProperties;
import com.aetheri.infrastructure.config.properties.KakaoProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KakaoHandler {
    private final SignInService signInService;

    private String clientId;
    private String redirectUri;
    private String refreshTokenCookie;
    private String accessTokenHeader;

    public KakaoHandler(SignInService signInService, KakaoProperties kakaoProperties, JWTProperties jwtProperties) {
        this.signInService = signInService;
        this.clientId = kakaoProperties.clientId();
        this.redirectUri = kakaoProperties.redirectUri();
        this.refreshTokenCookie = jwtProperties.refreshTokenCookie();
        this.accessTokenHeader = jwtProperties.accessTokenHeader();
    }

    /**
     * 인가 코드 요청 API.
     * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-code">카카오 REST API</a>
     *
     * @implNote 리다이렉트를 걸어주는 것이기 때문에 핸들러에서 처리하도록 함. 만약 리다이랙트 URI 생성을 분리한다면 분리하여도 됨.
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
        String code = request.queryParam("code")
                .orElseThrow(() -> new BusinessException(
                        ErrorMessage.NOT_FOUND_AUTHORIZATION_CODE,
                        "인증 코드를 응답에서 찾을 수 없습니다."
                ));

        return signInService.login(code).flatMap(response -> {
            log.info("[KakaoHandler] login: accessToken={}, refreshToken={}", response.accessToken(), response.refreshToken());
            ResponseCookie cookie = ResponseCookie.from(refreshTokenCookie, response.refreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(response.refreshTokenExpirationTime())
                    .build();

            return ServerResponse.ok().header(accessTokenHeader, response.accessToken())
                    .cookie(cookie)
                    .body(Mono.empty(), Void.class);
        });
    }

    public Mono<ServerResponse> myPate(ServerRequest request) {
        return request.principal() // Mono<Principal>을 반환
                .flatMap(principal -> {
                    // Principal 객체를 Authentication으로 캐스팅
                    Authentication authentication = (Authentication) principal;

                    // 사용자의 권한(역할) 가져오기
                    String roles = authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(", "));

                    // 사용자 정보(예: JWT subject, 유저 ID) 가져오기
                    // authentication.getPrincipal()은 보통 UserDetails 객체 또는 String을 반환
                    Object principalDetails = authentication.getPrincipal();
                    String username = null;
                    if (principalDetails instanceof String) {
                        username = (String) principalDetails;
                    }

                    // 로그를 찍거나 비즈니스 로직에 사용
                    log.info("인증된 사용자: {}, 역할: {}", username, roles);

                    return ServerResponse.ok().bodyValue("안녕하세요, " + username + "님! 당신의 역할은 " + roles + "입니다.");
                })
                // 인증되지 않은 요청일 경우 401 Unauthorized 응답
                .switchIfEmpty(ServerResponse.status(401).bodyValue("인증되지 않은 요청입니다."));
    }
}