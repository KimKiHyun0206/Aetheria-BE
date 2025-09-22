package com.aetheri.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "image")
public record ImageProperties(
        // 이미지 저장 경로를 저장하기 위한 프로퍼티
        String path
) {
}
