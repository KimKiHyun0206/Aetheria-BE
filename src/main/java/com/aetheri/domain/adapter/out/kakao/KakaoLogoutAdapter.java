package com.aetheri.domain.adapter.out.kakao;

import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KakaoLogoutAdapter {

    private final WebClient webClient;

    public KakaoLogoutAdapter(@Qualifier("kakaoWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 로그아웃 API
     * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#logout">카카오 REST API</a>
     * @param accessToken 카카오에서 발급해준 AccessToken
     * */
    public Mono<Void> logout(String accessToken) {
        return webClient.post()
                .uri("/v1/user/logout")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new BusinessException(
                                ErrorMessage.INVALID_REQUEST_PARAMETER,
                                "Invalid Parameter"
                        ))
                )
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> Mono.error(new BusinessException(
                                ErrorMessage.INTERNAL_SERVER_ERROR,
                                "Server Error"))
                )
                .bodyToMono(Void.class);
    }
}