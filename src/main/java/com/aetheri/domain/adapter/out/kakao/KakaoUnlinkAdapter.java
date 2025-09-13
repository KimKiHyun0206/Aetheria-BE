package com.aetheri.domain.adapter.out.kakao;

import com.aetheri.application.dto.UnlinkResponse;
import com.aetheri.application.port.out.kakao.KakaoUnlinkPort;
import com.aetheri.infrastructure.handler.WebClientErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 회원 탈퇴를 하기 위한 API
 */
@Service
public class KakaoUnlinkAdapter implements KakaoUnlinkPort {
    private final WebClient webClient;

    public KakaoUnlinkAdapter(@Qualifier("kakaoWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 연결 해제 API
     * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#unlink">카카오 REST API</a>
     *
     * @param accessToken 카카오에서 발급해주는 AccessToken
     */
    @Override
    public Mono<Long> unlink(String accessToken) {
        return webClient.post()
                .uri("/v1/user/unlink")
                .headers(s -> s.setBearerAuth(accessToken))
                .exchangeToMono(WebClientErrorHandler.handleErrors(UnlinkResponse.class))
                .map(UnlinkResponse::id);
    }
}