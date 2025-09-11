package com.aetheri.application.port.out.r2dbc;

import com.aetheri.infrastructure.persistence.KakaoToken;
import reactor.core.publisher.Mono;

public interface KakaoTokenRepositortyPort {
    Mono<Void> save(Long runnerId, String accessToken, String refreshToken);
    Mono<KakaoToken> findByRunnerId(Long runnerId);
    Mono<Void> deleteByRunnerId(Long runnerId);
    Mono<Boolean> existByRunnerId(Long runnerId);
}