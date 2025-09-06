package com.aetheri.interfaces.web.handler;

import com.aetheri.application.port.out.kakao.KakaoGetAccessTokenClient;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoHandler {
    private final KakaoGetAccessTokenClient kakaoGetAccessTokenClient;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    /**
     * 인가 코드 요청 API.
     * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-code">카카오 REST API</a>
     *
     * @implNote 리다이렉트를 걸어주는 것이기 때문에 핸들러에서 처리하도록 함. 만약 리다이랙트 URI 생성을 분리한다면 분리하여도 됨.
     * */
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
     * */
    public Mono<ServerResponse> getKakaoAccessToken(ServerRequest request) {
        String code = request.queryParam("code")
                .orElseThrow(() -> new BusinessException(
                        ErrorMessage.NOT_FOUND_AUTHORIZATION_CODE,
                        "인증 코드를 응답에서 찾을 수 없습니다."
                ));

        return kakaoGetAccessTokenClient.tokenRequest(code)
                .flatMap(accessToken -> {
                    log.info(accessToken.toString());
                    return ServerResponse.ok().bodyValue(Mono.empty());
                });
    }
}