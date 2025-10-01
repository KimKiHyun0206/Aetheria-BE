package com.aetheri;

import com.aetheri.infrastructure.config.properties.ImageProperties;
import com.aetheri.infrastructure.config.properties.JWTProperties;
import com.aetheri.infrastructure.config.properties.KakaoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * **Aetheria 백엔드 애플리케이션의 메인 진입점** 클래스입니다.
 *
 * <p>{@code @SpringBootApplication}을 통해 Spring Boot의 자동 구성, 컴포넌트 스캔,
 * 설정 클래스 정의를 활성화합니다.</p>
 *
 * <p>{@code @EnableConfigurationProperties}를 사용하여 애플리케이션 구동 시
 * 설정 파일(예: {@code application.yml})의 특정 속성들을 매핑하는 설정 클래스들을 활성화합니다.</p>
 */
@EnableConfigurationProperties({
        JWTProperties.class, // JWT 관련 설정 속성 활성화
        KakaoProperties.class, // 카카오 인증 관련 설정 속성 활성화
        ImageProperties.class // 이미지 저장 및 조회 관련 설정 속성 활성화
})
@SpringBootApplication
public class AetheriaBeApplication {

    /**
     * Spring Boot 애플리케이션을 실행하는 메인 메서드입니다.
     *
     * @param args 커맨드 라인 인자입니다.
     */
    public static void main(String[] args) {
        SpringApplication.run(AetheriaBeApplication.class, args);
    }
}