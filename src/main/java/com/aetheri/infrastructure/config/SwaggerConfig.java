package com.aetheri.infrastructure.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc 라이브러리를 사용하여 OpenAPI 3.0(Swagger) 문서화를 설정하는 클래스입니다.
 *
 * <p>이 설정은 애플리케이션의 모든 REST API 엔드포인트를 하나의 그룹으로 묶어 문서화합니다.</p>
 */
@Configuration
public class SwaggerConfig {

    /**
     * 애플리케이션의 REST API 엔드포인트를 위한 {@link GroupedOpenApi} 빈을 정의합니다.
     *
     * <p>API 경로 중 {@code /api/**} 패턴을 따르는 모든 컨트롤러를 하나의 문서 그룹({@code REST API})으로 묶습니다.</p>
     *
     * @return REST API 그룹화 설정이 완료된 {@code GroupedOpenApi} 인스턴스입니다.
     */
    @Bean
    public GroupedOpenApi userOpenApi() {
        return GroupedOpenApi.builder()
                .group("REST API") // 문서 그룹의 이름 설정
                .pathsToMatch("/api/**") // 문서화할 API 경로 패턴 지정
                .build();
    }
}