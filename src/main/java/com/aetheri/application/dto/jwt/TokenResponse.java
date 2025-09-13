package com.aetheri.application.dto.jwt;

/**
 * 서버 내부에서 리프레쉬 토큰을 사용하여 액세스 토큰이 재발급되었을 때 이를 리턴하기 위한 DTO
 * 클라이언트에게 반환하지 않는 DTO.
 */
public record TokenResponse(
        String accessToken,
        RefreshTokenIssueResponse refreshTokenIssueResponse
) {
    public static TokenResponse of(String accessToken, RefreshTokenIssueResponse refreshTokenIssueResponse) {
        return new TokenResponse(accessToken, refreshTokenIssueResponse);
    }
}