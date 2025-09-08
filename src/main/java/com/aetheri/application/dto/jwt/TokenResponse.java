package com.aetheri.application.dto.jwt;

public record TokenResponse(
        String accessToken,
        RefreshTokenIssueResponse refreshTokenIssueResponse
) {
    public static TokenResponse of(String accessToken, RefreshTokenIssueResponse refreshTokenIssueResponse) {
        return new TokenResponse(accessToken, refreshTokenIssueResponse);
    }
}