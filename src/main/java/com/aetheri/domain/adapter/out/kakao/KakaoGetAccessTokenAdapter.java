package com.aetheri.domain.adapter.out.kakao;

import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.interfaces.dto.kakao.KakaoTokenResponseDto;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 카카오 로그인 API를 사용하여 액세스 토큰을 가져오기 위한 서비스
 */
@Service
public class KakaoGetAccessTokenAdapter {

    @Value("${kakao.client_id}")
    private String clientId;

    private final WebClient kakaoAuthWebClient;

    public KakaoGetAccessTokenAdapter(@Qualifier("kakaoAuthWebClient") WebClient kakaoAuthWebClient) {
        this.kakaoAuthWebClient = kakaoAuthWebClient;
    }

    /**
     * 카카오 Access 토큰 발급을 위한 요청 코드
     * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token">카카오 REST API</a>
     * @param code 카카오에서 발급해준 인증 코드
     * */
    public Mono<KakaoTokenResponseDto> tokenRequest(String code) {
        return kakaoAuthWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp ->
                                Mono.error(new BusinessException(
                                        ErrorMessage.INVALID_REQUEST_PARAMETER,
                                        "Invalid Parameter"
                                ))
                )
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp ->
                                Mono.error(new BusinessException(
                                        ErrorMessage.INTERNAL_SERVER_ERROR,
                                        "Internal Server Error"
                                ))
                )
                .bodyToMono(KakaoTokenResponseDto.class);
    }
}