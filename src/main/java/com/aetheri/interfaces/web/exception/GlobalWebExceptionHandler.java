package com.aetheri.interfaces.web.exception;

import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 전역(글로벌) 예외 처리를 위한 클래스.
 * */
@Slf4j
@Order(-2)
@Component
public class GlobalWebExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status;
        String code;
        String message;

        if (ex instanceof BusinessException businessEx) {
            ErrorMessage errorMessage = businessEx.getErrorMessage();
            status = errorMessage.getStatus();
            code = errorMessage.name();
            message = errorMessage.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = "INTERNAL_SERVER_ERROR";
            message = "예상치 못한 오류가 발생했습니다.";
        }

        log.error("[GlobalWebExceptionHandler] 오류가 발생했습니다: {}", ex.getMessage());

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorBody = Map.of(
                "status", status.value(),
                "code", code,
                "message", message
        );

        try {
            String errorJson = objectMapper.writeValueAsString(errorBody);
            DataBuffer buffer = response.bufferFactory().wrap(errorJson.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("[GlobalWebExceptionHandler] 서버에서 예외 정보를 JSON 문자열로 변환하는 과정에서 문제가 발생: {}", e.getMessage());
            String fallbackJson = "{\"status\":500,\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"예상치 못한 오류가 발생했습니다.\"}";
            DataBuffer buffer = response.bufferFactory().wrap(fallbackJson.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }
    }
}