package com.aetheri.interfaces.web.handler;

import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class MyHandler {
    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("message", "Hello, World!"));
    }

    public Mono<ServerResponse> helloError(ServerRequest request) {
        // 의도적으로 예외 발생
        return Mono.error(
                new BusinessException(
                        ErrorMessage.INTERNAL_SERVER_ERROR,
                        "서버에서 발생한 오류"
                )
        );
    }
}