package com.aetheri.infrastructure.handler;

import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
public class WebClientErrorHandler {

    public static <T> Function<ClientResponse, Mono<T>> handleErrors(Class<T> responseType) {
        return response -> {
            if (response.statusCode().is4xxClientError()) {
                return Mono.error(new BusinessException(
                        ErrorMessage.INVALID_REQUEST_PARAMETER,
                        "올바르지 않은 파라미터로 인해 요청이 거부되었습니다."
                ));
            }
            if (response.statusCode().is5xxServerError()) {
                return Mono.error(new BusinessException(
                        ErrorMessage.INTERNAL_SERVER_ERROR,
                        "서버 에러로 요청이 거부되었습니다."
                ));
            }
            return response.bodyToMono(responseType);
        };
    }
}