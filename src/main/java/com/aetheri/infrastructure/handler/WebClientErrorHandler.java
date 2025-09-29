package com.aetheri.infrastructure.handler;

import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * Spring {@code WebClient}의 응답을 처리하고, HTTP 오류 상태 코드(4xx, 5xx)를
 * 애플리케이션의 표준화된 **{@link BusinessException}으로 변환**하는 정적 핸들러 클래스입니다.
 *
 * <p>주로 {@code WebClient.exchangeToMono()}와 함께 사용하여 외부 API 호출의 오류 처리 흐름을 간소화합니다.</p>
 */
@Component
public class WebClientErrorHandler {

    /**
     * {@code WebClient}의 {@code ClientResponse}를 받아서 오류 상태 코드를 확인하고
     * 적절한 {@code Mono<T>}를 반환하는 {@code Function}을 생성합니다.
     *
     * <p>오류가 없을 경우, 응답 본문을 주어진 {@code responseType}으로 변환하여 발행합니다.</p>
     * <ul>
     * <li>4xx 클라이언트 오류 발생 시: {@code INVALID_REQUEST_PARAMETER}와 함께 {@code BusinessException}을 발생시킵니다.</li>
     * <li>5xx 서버 오류 발생 시: {@code INTERNAL_SERVER_ERROR}와 함께 {@code BusinessException}을 발생시킵니다.</li>
     * <li>정상 응답 시 (2xx): 응답 본문을 {@code Mono<T>}로 변환합니다.</li>
     * </ul>
     *
     * @param responseType 응답 본문을 변환할 대상 클래스 타입입니다.
     * @param <T> 응답 본문 타입입니다.
     * @return {@code ClientResponse}를 {@code Mono<T>}로 변환하는 {@code Function}입니다.
     */
    public static <T> Function<ClientResponse, Mono<T>> handleErrors(Class<T> responseType) {
        return response -> {
            // 4xx 클라이언트 오류 처리
            if (response.statusCode().is4xxClientError()) {
                return Mono.error(new BusinessException(
                        ErrorMessage.INVALID_REQUEST_PARAMETER,
                        "올바르지 않은 파라미터로 인해 요청이 거부되었습니다. (HTTP Status: " + response.statusCode().value() + ")"
                ));
            }
            // 5xx 서버 오류 처리
            if (response.statusCode().is5xxServerError()) {
                return Mono.error(new BusinessException(
                        ErrorMessage.INTERNAL_SERVER_ERROR,
                        "외부 서버 에러로 요청이 거부되었습니다. (HTTP Status: " + response.statusCode().value() + ")"
                ));
            }
            // 정상 응답 (2xx) 처리: 응답 본문을 원하는 타입으로 변환
            return response.bodyToMono(responseType);
        };
    }
}