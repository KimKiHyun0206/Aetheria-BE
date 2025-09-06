package com.aetheri.application.port.out.kakao;

import com.aetheri.application.dto.UnlinkResponse;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 회원 탈퇴를 하기 위한 API
 * */
@Service
public class KakaoUnlinkClient {

    private final WebClient webClient;

    public KakaoUnlinkClient(@Qualifier("kakaoWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 연결 해제 API
     * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#unlink">카카오 REST API</a>
     * @param accessToken 카카오에서 발급해주는 AccessToken
     * */
    public Mono<Long> unlink(String accessToken) {
        return webClient.post()
                .uri("/v1/user/unlink")
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
                .bodyToMono(UnlinkResponse.class)
                .map(UnlinkResponse::id);
    }
}