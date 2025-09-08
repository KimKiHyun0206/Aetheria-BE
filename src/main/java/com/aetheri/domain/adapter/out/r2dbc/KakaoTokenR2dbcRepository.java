package com.aetheri.domain.adapter.out.r2dbc;

import com.aetheri.infrastructure.persistence.KakaoToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface KakaoTokenR2dbcRepository extends R2dbcRepository<KakaoToken, Long> {
    Mono<KakaoToken> findByRunnerId(Long runnerId);
    Mono<Void> deleteByRunnerId(Long runnerId);
    Mono<Boolean> existsByRunnerId(Long runnerId);
}