package com.aetheri.infrastructure.persistence;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("kakao_token")
@NoArgsConstructor
public class KakaoToken {
    @Id
    private Long id;
    private Long runnerId;
    private String accessToken;
    private String refreshToken;

    @Builder
    private KakaoToken(Long runnerId, String accessToken, String refreshToken) {
        this.runnerId = runnerId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static KakaoToken toEntity(Long runnerId, String accessToken, String refreshToken) {
        return KakaoToken.builder()
                .runnerId(runnerId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}