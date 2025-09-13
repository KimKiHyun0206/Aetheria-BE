package com.aetheri.domain.adapter.out.kakao;

import com.aetheri.application.port.out.kakao.KakaoLogoutPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.config.properties.JWTProperties;
import com.aetheri.infrastructure.handler.WebClientErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KakaoLogoutAdapter implements KakaoLogoutPort {

    private final WebClient webClient;

    private final String AUTHORIZATION_HEADER;

    public KakaoLogoutAdapter(@Qualifier("kakaoWebClient") WebClient webClient, JWTProperties jwtProperties) {
        this.webClient = webClient;
        AUTHORIZATION_HEADER = jwtProperties.accessTokenHeader();
    }

    /**
     * 로그아웃 API
     * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#logout">카카오 REST API</a>
     *
     * @param accessToken 카카오에서 발급해준 AccessToken
     */
    @Override
    public Mono<Void> logout(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return Mono.error(new BusinessException(
                    ErrorMessage.INVALID_REQUEST_PARAMETER,
                    "로그아웃에 필요한 액세스 토큰이 비어있습니다.")
            );
        }

        return webClient.post()
                .uri("/v1/user/logout")
                .header(AUTHORIZATION_HEADER, "Bearer " + accessToken)
                .exchangeToMono(WebClientErrorHandler.handleErrors(Void.class));
    }
}