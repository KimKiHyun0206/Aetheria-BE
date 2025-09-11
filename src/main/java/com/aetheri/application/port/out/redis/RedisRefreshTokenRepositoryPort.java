package com.aetheri.application.port.out.redis;

import reactor.core.publisher.Mono;

public interface RedisRefreshTokenRepositoryPort {
    Mono<Boolean> saveRefreshToken(Long userId, String refreshToken);
    Mono<String> getRefreshToken(Long userId);
    Mono<Boolean> deleteRefreshToken(Long userId);
}