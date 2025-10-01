package com.aetheri.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Spring Data Redis를 사용하기 위한 **반응형(Reactive) 설정 클래스**입니다.
 *
 * <p>이 설정은 {@link ReactiveRedisConnectionFactory}를 사용하여 Redis와의 비동기/논블로킹
 * 통신을 가능하게 하며, {@code String} 키와 {@code String} 값을 사용하는
 * {@link ReactiveRedisTemplate} 빈을 정의합니다.</p>
 */
@Configuration
public class RedisConfig {

    /**
     * Redis 연결 팩토리를 사용하여 {@code String} 키와 {@code String} 값을 처리하는
     * **주요(Primary) {@link ReactiveRedisTemplate} 빈**을 생성합니다.
     *
     * <p>이 템플릿은 키와 값 모두에 대해 {@link RedisSerializer#string()}를 사용하여
     * 직렬화 컨텍스트를 구성하므로, 일반 문자열 데이터를 Redis에 저장하고 조회하는 데 적합합니다.</p>
     *
     * @param factory Redis 연결을 관리하는 반응형 연결 팩토리입니다.
     * @return {@code String} 키/값을 위한 설정이 완료된 {@code ReactiveRedisTemplate} 인스턴스입니다.
     */
    @Bean
    @Primary
    public ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        // 키와 값 모두 String 직렬화 방식을 사용하도록 컨텍스트 설정
        RedisSerializationContext<String, String> context =
                RedisSerializationContext.<String, String>newSerializationContext(RedisSerializer.string())
                        .value(RedisSerializer.string()) // 값 직렬화 설정
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}