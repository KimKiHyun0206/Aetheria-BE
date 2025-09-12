package com.aetheri.domain.adapter.out.kakao;

import com.aetheri.application.port.out.kakao.KakaoLogoutPort;
import com.aetheri.infrastructure.handler.WebClientErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KakaoLogoutAdapter implements KakaoLogoutPort {

    private final WebClient webClient;

    public KakaoLogoutAdapter(@Qualifier("kakaoWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 로그아웃 API
     * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#logout">카카오 REST API</a>
     *
     * @param accessToken 카카오에서 발급해준 AccessToken
     */
    @Override
    public Mono<Void> logout(String accessToken) {
        return webClient.post()
                .uri("/v1/user/logout")
                .header("Authorization", "Bearer " + accessToken)
                .exchangeToMono(WebClientErrorHandler.handleErrors(Void.class));
    }
}