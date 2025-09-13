package com.aetheri.domain.adapter.out.kakao;

import com.aetheri.application.port.out.kakao.KakaoUserInformationInquiryPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.handler.WebClientErrorHandler;
import com.aetheri.interfaces.dto.kakao.KakaoUserInfoResponseDto;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 카카오 API를 사용해서 사용자 정보를 가져오기 위한 서비스
 */
@Slf4j
@Service
public class KakaoUserInformationInquiryAdapter implements KakaoUserInformationInquiryPort {

    private final WebClient webClient;

    public KakaoUserInformationInquiryAdapter(@Qualifier("kakaoWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 사용자 정보를 가져오기 위한 API
     * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info">카카오 REST API</a>
     */
    public Mono<KakaoUserInfoResponseDto> userInformationInquiry(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return Mono.error(new BusinessException(
                    ErrorMessage.INVALID_REQUEST_PARAMETER,
                    "사용자 조회에 필요한 액세스 토큰이 비어있습니다."
            ));
        }

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .headers(s -> s.setBearerAuth(accessToken)) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .exchangeToMono(WebClientErrorHandler.handleErrors(KakaoUserInfoResponseDto.class));
    }
}