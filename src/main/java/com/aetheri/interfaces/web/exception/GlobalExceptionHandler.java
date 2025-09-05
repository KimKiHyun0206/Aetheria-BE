package com.aetheri.interfaces.web.exception;

import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.interfaces.dto.error.ErrorResponseDto;
import io.r2dbc.spi.R2dbcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * 전역(글로벌) 예외 처리를 위한 클래스.
 * */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /*
     * - 예기치 못하게 발생하는 예외만 처리하도록 함.
     * - 데이터베이스 커넥션 오류, 유효성 검사 실패 등 예측 불가능한 오류만 처리.
     * - 복구 불가능한 예외를 처리하도록 함.
     * */
    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleRuntimeException(BusinessException e) {

        log.error("[Error] RuntimeException -> {}", e.getMessage());

        return Mono.just(ErrorResponseDto.of(e.getErrorMessage()));
    }

    /**
     * 데이터베이스 연결 중 발생하는 예외는 따로 처리하도록 함.
     * */
    @ExceptionHandler({DataAccessException.class, R2dbcException.class})
    public Mono<ResponseEntity<ErrorResponseDto>> handleDataAccessException(Exception e) {
        log.error("[Error] DataAccessException -> {}", e.getMessage());
        return Mono.just(ErrorResponseDto.of(ErrorMessage.R2DBC_CONNECTION_ERROR));
    }
}