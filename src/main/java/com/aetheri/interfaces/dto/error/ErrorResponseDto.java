package com.aetheri.interfaces.dto.error;

import com.aetheri.domain.exception.message.ErrorMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * 애플리케이션에서 발생하는 오류를 **클라이언트에게 일관된 형식으로 응답**하기 위한 DTO(Data Transfer Object)입니다.
 *
 * <p>이 클래스는 {@link ErrorMessage}에 정의된 오류 정보를 사용하여 HTTP 상태 코드와 응답 본문을 구성합니다.</p>
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponseDto {
    /**
     * 오류의 고유 식별 코드입니다. (예: {@code ErrorMessage}의 이름)
     */
    private final String code;
    /**
     * 사용자 또는 개발자에게 제공될 상세 오류 메시지입니다.
     */
    private final String message;
    /**
     * 오류가 발생하여 응답이 생성된 서버 시간입니다.
     */
    private final LocalDateTime serverDateTime;
    /**
     * 오류에 해당하는 HTTP 상태 코드의 숫자 값입니다. (예: 400, 404, 500)
     */
    private final int status;

    /**
     * 주어진 {@link ErrorMessage} 객체를 사용하여 **표준화된 HTTP 에러 응답({@code ResponseEntity})**을 생성합니다.
     *
     * <p>이 메서드는 {@code ErrorMessage}의 HTTP 상태 코드를 응답 상태로 설정하고,
     * 응답 본문에는 {@code ErrorResponseDto} 인스턴스를 담습니다.</p>
     *
     * @param message 오류의 유형, 메시지, 상태 코드를 포함하는 {@code ErrorMessage}입니다.
     * @return 클라이언트에게 반환될 {@code ResponseEntity<ErrorResponseDto>}입니다.
     */
    public static ResponseEntity<ErrorResponseDto> of(ErrorMessage message) {
        return ResponseEntity
                .status(message.getStatus())
                .body(new ErrorResponseDto(
                        message.name(),
                        message.getMessage(),
                        LocalDateTime.now(),
                        message.getStatus().value()
                ));
    }
}