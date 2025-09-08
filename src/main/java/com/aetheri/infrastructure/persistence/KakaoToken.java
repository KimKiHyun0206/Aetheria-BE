package com.aetheri.infrastructure.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("kakao_token")
@NoArgsConstructor
public class KakaoToken {
    private Long id;
    private Long runner_id;
    private String accessToken;
    private String refreshToken;

    public KakaoToken(Long runner_id, String accessToken, String refreshToken) {
        this.runner_id = runner_id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}