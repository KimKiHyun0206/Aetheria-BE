package com.aetheri.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * **이미지 관련 설정 값**을 외부 설정 파일(예: application.yml)로부터 주입받기 위한 레코드 클래스입니다.
 *
 * <p>설정 파일에서 {@code image} 프리픽스({@code prefix = "image"})로 시작하는 속성들을
 * 이 레코드의 필드에 자동으로 바인딩합니다.</p>
 *
 * @param path 이미지가 저장되는 **기본 경로(로컬 파일 시스템 경로 또는 외부 스토리지 버킷 이름)**를 저장하는 프로퍼티입니다.
 */
@ConfigurationProperties(prefix = "image")
public record ImageProperties(
        // 이미지 저장 경로를 저장하기 위한 프로퍼티
        String path
) {
}