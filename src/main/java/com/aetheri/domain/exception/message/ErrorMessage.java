package com.aetheri.domain.exception.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 메시지를 관리하기 위한 Enum 클래스.
 * */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorMessage {
    // Server 에러
    INVALID_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예기치 못한 에러가 발생했습니다"),

    // 데이터베이스 커넥션 에러
    R2DBC_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 연결에 실패했습니다."),
    R2DBC_CONNECTION_ACQUISITION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 연결을 얻을 수 없습니다."),

    // 카카오 API
    NOT_FOUND_AUTHORIZATION_CODE(HttpStatus.NOT_FOUND, "인증 코드를 응답에서 찾을 수 없습니다"),


    // 사용자
    NOT_FOUND_RUNNER(HttpStatus.NOT_FOUND, "요청한 사용자를 찾지 못했습니다."),
    DUPLICATE_RUNNER(HttpStatus.CONFLICT, "중복된 사용자입니다."),
    ;


    private final HttpStatus status;
    private final String message;
}