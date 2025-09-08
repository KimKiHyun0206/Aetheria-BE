package com.aetheri.domain.adapter.out.redis;

import com.aetheri.application.dto.jwt.TokenResponse;
import com.aetheri.application.port.out.redis.RedisRefreshTokenRepositoryPort;
import com.aetheri.infrastructure.config.properties.JWTProperties;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RedisRefreshTokenAdapter implements RedisRefreshTokenRepositoryPort {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final JWTProperties jwtProperties;

    public RedisRefreshTokenAdapter(
            ReactiveRedisTemplate<String, String> redisTemplate,
            JWTProperties jwtProperties
    ) {
        this.redisTemplate = redisTemplate;
        this.jwtProperties = jwtProperties;
    }

    /**
     * 리프레시 토큰 저장
     */
    @Override
    public Mono<Boolean> saveRefreshToken(Long userId, String refreshToken) {
        String key = buildKey(userId);
        Duration ttl = Duration.ofDays(jwtProperties.refreshTokenExpirationDays());

        return redisTemplate.opsForValue()
                .set(key, refreshToken, ttl);
    }

    /**
     * 리프레시 토큰 조회
     */
    @Override
    public Mono<String> getRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get(buildKey(userId));
    }

    /**
     * 리프레시 토큰 삭제 (로그아웃 시)
     */
    @Override
    public Mono<Boolean> deleteRefreshToken(Long userId) {
        return redisTemplate.opsForValue().delete(buildKey(userId));
    }


    private String buildKey(Long userId) {
        return jwtProperties.redis().key().prefix() + userId + jwtProperties.redis().key().suffix();
    }
}
