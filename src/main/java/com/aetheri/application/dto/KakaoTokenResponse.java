package com.aetheri.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 API로부터 발급받은 토큰을 응답하는 DTO.
 * 클라이언트에게 응답하면 안 됩니다.
 */
public record KakaoTokenResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("expires_in")
        Integer expiresIn,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("refresh_token_expires_in")
        Integer refreshTokenExpiresIn
) {
}