package com.aetheri.application.util;

import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationUtils {
    public static void validateNotEmpty(String value, ErrorMessage error, String message) {
        if (value == null || value.isEmpty()) {
            throw new BusinessException(error, message);
        }
    }
}