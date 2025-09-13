package com.aetheri.domain.adapter.out.kakao;

import com.aetheri.application.dto.KakaoTokenResponse;
import com.aetheri.application.port.out.kakao.KakaoRefreshTokenPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.config.properties.KakaoProperties;
import com.aetheri.infrastructure.handler.WebClientErrorHandler;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class KakaoRefreshTokenAdapter implements KakaoRefreshTokenPort {
    private final String clientId;
    private final WebClient webClient;

    public KakaoRefreshTokenAdapter(
            @Qualifier("kakaoAuthWebClient") WebClient webClient,
            KakaoProperties kakaoProperties
    ) {
        this.webClient = webClient;
        this.clientId = kakaoProperties.clientId();
    }

    @Override
    public Mono<KakaoTokenResponse> refreshAccessToken(String refreshToken) {
        log.info(refreshToken);
        if (refreshToken == null || refreshToken.isBlank()) {
            return Mono.error(new BusinessException(
                    ErrorMessage.INVALID_REFRESH_TOKEN,
                    "RefreshToken이 존재하지 않음")
            );
        }

        return webClient.method(HttpMethod.POST)
                .uri("/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .body(BodyInserters.fromFormData("grant_type", "refreshToken")
                        .with("client_id", clientId)
                        .with("refreshToken", refreshToken)
                )
                .exchangeToMono(WebClientErrorHandler.handleErrors(KakaoTokenResponse.class));
    }
}