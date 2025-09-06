package com.aetheri.interfaces.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenResponseDto(

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("id_token")
        String idToken,

        @JsonProperty("expires_in")
        Integer expiresIn,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("refresh_token_expires_in")
        Integer refreshTokenExpiresIn,

        @JsonProperty("scope")
        String scope
) {
    public String toDefaultLogString() {
        return "[ KakaoTokenResponseDto ]" +
                "\n{" +
                "\n\ttokenType : " + this.tokenType +
                "\n\taccessToken : " + this.accessToken +
                "\n\tidToken : " + this.idToken +
                "\n\texpiresIn : " + this.expiresIn +
                "\n\trefreshToken : " + this.refreshToken +
                "\n\trefreshTokenExpiresIn : " + this.refreshTokenExpiresIn +
                "\n\tscope : " + this.scope +
                "\n}";
    }
}