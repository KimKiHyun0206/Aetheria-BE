package com.aetheri.application.port.out.redis;

import com.aetheri.application.dto.jwt.TokenResponse;
import reactor.core.publisher.Mono;

public interface RedisRefreshTokenRepositoryPort {
    Mono<Boolean> saveRefreshToken(Long userId, String refreshToken);
    Mono<String> getRefreshToken(Long userId);
    Mono<Boolean> deleteRefreshToken(Long userId);
}