package com.aetheri.interfaces.dto.error;

import com.aetheri.domain.exception.message.ErrorMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * 에러 응답을 일관성있게 하기 위한 클래스.
 * */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponseDto {
    private final String code;
    private final String message;
    private final LocalDateTime serverDateTime;

    public static ResponseEntity<ErrorResponseDto> of(ErrorMessage message) {
        return ResponseEntity
                .status(message.getStatus())
                .body(new ErrorResponseDto(
                        message.getStatus().toString(),
                        message.getMessage(),
                        LocalDateTime.now())
                );
    }
}