package com.aetheri.domain.adapter.out.kakao;

import com.aetheri.application.port.out.kakao.KakaoUserInformationInquiryPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.interfaces.dto.kakao.KakaoUserInfoResponseDto;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 카카오 API를 사용해서 사용자 정보를 가져오기 위한 서비스
 */
@Slf4j
@Service
public class KakaoUserInformationInquiryAdapter implements KakaoUserInformationInquiryPort {

    private final WebClient kakaoWebClient;

    public KakaoUserInformationInquiryAdapter(@Qualifier("kakaoWebClient") WebClient kakaoWebClient) {
        this.kakaoWebClient = kakaoWebClient;
    }

    /**
     * 사용자 정보를 가져오기 위한 API
     * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info">카카오 REST API</a>
     */
    public Mono<KakaoUserInfoResponseDto> userInformationInquiry(String accessToken) {
        return kakaoWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
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
                .bodyToMono(KakaoUserInfoResponseDto.class);
    }
}