package com.aetheri.application.dto;

/**
 * 로그인 성공을 핸들러에게 응답하기 위한 DTO입니다.
 * 클라이언트에 응답하면 안 됩니다. 리프레쉬 토큰은 쿠키에 저장되어 리턴되어야 하고, 액세스 토큰은 헤더에 저장되어야합니다.
 */
public record SignInResponse(
        String accessToken,
        String refreshToken,
        long refreshTokenExpirationTime
) {
}