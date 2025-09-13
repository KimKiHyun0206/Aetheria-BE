package com.aetheri.application.port.out.r2dbc;

import com.aetheri.infrastructure.persistence.KakaoToken;
import reactor.core.publisher.Mono;

/**
 * 데이터베이스에 카카오 토큰(액세스 토큰/리프래쉬 토큰)을 저장하기 위한 데이터베이스 포트입니다.
 *
 * @see com.aetheri.domain.adapter.out.r2dbc.KakaoTokenRepositoryR2dbcAdapter
 */
public interface KakaoTokenRepositoryPort {

    /**
     * 카카오 토큰을 저장하기 위한 메소드
     *
     * @param runnerId 사용자의 ID
     */
    Mono<Void> save(Long runnerId, String accessToken, String refreshToken);

    /**
     * 사용자의 ID를 통해 카카오 토큰을 찾기 위한 메소드
     *
     * @param runnerId 사용자의 ID
     */
    Mono<KakaoToken> findByRunnerId(Long runnerId);

    /**
     * 사용자의 ID를 사용해 카카오 토큰을 지우기 위한 메소드
     *
     * @param runnerId 사용자의 ID
     */
    Mono<Void> deleteByRunnerId(Long runnerId);

    /**
     * 사용자의 ID를 사용해 카카오 토큰이 존재하는지 확인하기 위한 메소드
     *
     * @param runnerId 사용자의 ID
     */
    Mono<Boolean> existsByRunnerId(Long runnerId);
}