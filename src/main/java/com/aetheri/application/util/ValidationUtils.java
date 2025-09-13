package com.aetheri.application.util;

import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

/**
 * 애플리케이션 내에서 비어있거나 유효하지 않은 값을 검증하기 위한 유틸 클래스
 * */
@UtilityClass
public class ValidationUtils {
    public static void validateNotEmpty(String value, ErrorMessage error, String message) {
        if (value == null || value.isEmpty()) {
            throw new BusinessException(error, message);
        }
    }

    public static Mono<String> validateNotBlankMono(String value, ErrorMessage error, String message) {
        return Mono.justOrEmpty(value)
                .filter(v -> !v.isBlank())
                .switchIfEmpty(Mono.error(new BusinessException(error, message)));
    }
}