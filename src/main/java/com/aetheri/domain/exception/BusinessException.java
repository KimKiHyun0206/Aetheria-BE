package com.aetheri.domain.exception;

import com.aetheri.domain.exception.message.ErrorMessage;
import lombok.Getter;

/**
 * 애플리케이션의 비즈니스 로직 처리 과정에서 발생하는 **예측 가능한 오류**를 나타내는
 * 사용자 정의 런타임 예외({@code RuntimeException}) 클래스입니다.
 *
 * <p>이 예외는 표준화된 오류 코드와 HTTP 상태 코드를 포함하는 {@link ErrorMessage} 객체를
 * 필수로 가지며, 이를 통해 오류 처리 및 클라이언트 응답을 일관되게 처리할 수 있습니다.</p>
 */
@Getter
public class BusinessException extends RuntimeException{
    /**
     * 오류 유형, HTTP 상태 코드 및 기본 메시지를 정의하는 표준화된 {@link ErrorMessage}입니다.
     */
    private final ErrorMessage errorMessage;

    /**
     * 표준 {@link ErrorMessage}를 사용하여 예외를 생성합니다.
     *
     * <p>이 생성자를 사용하면 {@code super(message.getMessage())}를 호출하여
     * {@code RuntimeException}의 기본 메시지에 {@code ErrorMessage}의 기본 메시지를 설정합니다.</p>
     *
     * @param message 오류의 유형과 정보를 담고 있는 {@code ErrorMessage} 객체입니다.
     */
    public BusinessException(ErrorMessage message) {
        super(message.getMessage());
        this.errorMessage = message;
    }

    /**
     * 표준 {@link ErrorMessage}와 함께 오류에 대한 **추가적인 상세 이유**를 사용하여 예외를 생성합니다.
     *
     * <p>이 생성자는 {@code super(reason)}을 호출하여
     * {@code RuntimeException}의 메시지에 {@code reason}을 설정합니다.</p>
     *
     * @param message 오류의 유형과 정보를 담고 있는 {@code ErrorMessage} 객체입니다.
     * @param reason 예외 발생의 구체적이고 상세한 이유 문자열입니다.
     */
    public BusinessException(ErrorMessage message, String reason) {
        super(reason);
        this.errorMessage = message;
    }

    /**
     * 오류의 **이유 문자열({@code reason})만으로** 예외를 생성합니다.
     *
     * <p>이 경우, 내부적으로 {@link ErrorMessage#INTERNAL_SERVER_ERROR}를 기본 {@code errorMessage}로 설정합니다.
     * 이는 예외 유형을 명확히 지정하기 어려운 일반적인 시스템 오류 상황에 사용될 수 있습니다.</p>
     *
     * @param reason 예외 발생의 구체적이고 상세한 이유 문자열입니다.
     */
    public BusinessException(String reason) {
        super(reason);
        // 오류 메시지가 명시되지 않은 경우, 내부 서버 오류(INTERNAL_SERVER_ERROR)로 처리
        this.errorMessage = ErrorMessage.INTERNAL_SERVER_ERROR;
    }
}