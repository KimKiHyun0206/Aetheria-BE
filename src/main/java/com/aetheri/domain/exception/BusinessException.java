package com.aetheri.domain.exception;

import com.aetheri.domain.exception.message.ErrorMessage;
import lombok.Getter;

/**
 * 에러를 감싸 원하는대로 반환하기 위한 예외 클래스.
 * */
@Getter
public class BusinessException extends RuntimeException{
    private final ErrorMessage errorMessage;

    /**
     * ErrorMessage를 사용하여 예외를 생성합니다.
     *
     * @param message 오류 메시지
     * */
    public BusinessException(ErrorMessage message) {
        super(message.getMessage());
        this.errorMessage = message;
    }

    /**
     * ErrorMessage와 추가 이유를 사용하여 예외를 생성합니다.
     *
     * @param message 오휴 메시지
     * @param reason 추가 이유
     * */
    public BusinessException(ErrorMessage message, String reason) {
        super(reason);
        this.errorMessage = message;
    }

    /**
     * 이유만으로 예외를 생성한다.
     *
     * @param reason 예외 발생 이유
     * */
    public BusinessException(String reason) {
        super(reason);
        this.errorMessage = ErrorMessage.INTERNAL_SERVER_ERROR;
    }
}