package com.aetheri.infrastructure.persistence.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("kakao_token")
@NoArgsConstructor
public class KakaoToken {
    @Id
    @Column("id")
    private Long id;

    @Column("runner_id")
    private Long runnerId;

    @Column("access_token")
    private String accessToken;

    @Column("refresh_token")
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