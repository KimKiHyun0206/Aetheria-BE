package com.aetheri.application.port.out.redis;

import reactor.core.publisher.Mono;

/**
 * Redis에 리프래쉬 토큰을 관리하기 위한 포트
 *
 * @see com.aetheri.domain.adapter.out.redis.RedisRefreshTokenAdapter
 */
public interface RedisRefreshTokenRepositoryPort {

    /**
     * 리프레쉬 토큰을 저장하기 위한 메소드
     *
     * @param refreshToken 저장할 리프래쉬 토큰
     * @param userId       사용자의 ID
     */
    Mono<Boolean> saveRefreshToken(Long userId, String refreshToken);

    /**
     * 리프래쉬 토큰을 조회하기 위한 메소드
     *
     * @param userId 사용자의 ID
     */
    Mono<String> getRefreshToken(Long userId);

    /**
     * 리프래쉬 토큰을 삭제하기 위한 메소드
     *
     * @param userId 사용자의 ID
     */
    Mono<Boolean> deleteRefreshToken(Long userId);
}