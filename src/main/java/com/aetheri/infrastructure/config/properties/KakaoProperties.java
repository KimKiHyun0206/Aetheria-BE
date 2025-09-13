package com.aetheri.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoProperties(

        // 응답 받을 클라이언트의 ID
        String clientId,

        // 로그인 후 카카오에서 설정한 리다이렉트 주소
        String redirectUri,

        // 인증 API의 prefix
        String authApi,

        // 일반 API의 prefix
        String api,

        // 카카오 API 응답이 오는 데 걸리는 허용 시간
        int responseTimeoutSeconds,

        // 연결 유지를 허용하는 시간
        int connectTimeoutMillis,

        int timeOutSeconds
) {
}
