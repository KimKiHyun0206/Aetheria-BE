package com.aetheri.application.dto;

public record SignInResponse(
        String accessToken,
        String refreshToken,
        long refreshTokenExpirationTime
) {
}