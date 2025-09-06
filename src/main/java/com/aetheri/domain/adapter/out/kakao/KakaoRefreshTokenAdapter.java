package com.aetheri.domain.adapter.out.kakao;

import com.aetheri.application.dto.KakaoTokenResponse;
import com.aetheri.application.port.out.kakao.KakaoRefreshTokenPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.config.properties.KakaoProperties;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KakaoRefreshTokenAdapter implements KakaoRefreshTokenPort {

    private final String clientId;

    private final WebClient webClient;

    public KakaoRefreshTokenAdapter(
            @Qualifier("kakaoWebClient") WebClient webClient,
            KakaoProperties kakaoProperties
    ) {
        this.webClient = webClient;
        this.clientId = kakaoProperties.clientId();
    }

    @Override
    public Mono<KakaoTokenResponse> refreshAccessToken(String refreshToken) {
        return webClient.method(HttpMethod.POST)
                .uri("/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                        .with("client_id", clientId)
                        .with("refresh_token", refreshToken)
                )
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
                .bodyToMono(KakaoTokenResponse.class);
    }
}