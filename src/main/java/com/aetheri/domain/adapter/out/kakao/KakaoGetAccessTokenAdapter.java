package com.aetheri.domain.adapter.out.kakao;

import com.aetheri.application.dto.KakaoTokenResponse;
import com.aetheri.application.port.out.kakao.KakaoGetAccessTokenPort;
import com.aetheri.infrastructure.config.properties.KakaoProperties;
import com.aetheri.infrastructure.handler.WebClientErrorHandler;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 카카오 로그인 API를 사용하여 액세스 토큰을 가져오기 위한 서비스
 */
@Service
public class KakaoGetAccessTokenAdapter implements KakaoGetAccessTokenPort {

    private final String clientId;
    private final WebClient webClient;

    public KakaoGetAccessTokenAdapter(
            @Qualifier("kakaoAuthWebClient") WebClient webClient,
            KakaoProperties kakaoProperties
    ) {
        this.webClient = webClient;
        this.clientId = kakaoProperties.clientId();
    }

    /**
     * 카카오 Access 토큰 발급을 위한 요청 코드
     * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token">카카오 REST API</a>
     *
     * @param code 카카오에서 발급해준 인증 코드
     */
    @Override
    public Mono<KakaoTokenResponse> tokenRequest(String code) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .exchangeToMono(WebClientErrorHandler.handleErrors(KakaoTokenResponse.class));
    }
}