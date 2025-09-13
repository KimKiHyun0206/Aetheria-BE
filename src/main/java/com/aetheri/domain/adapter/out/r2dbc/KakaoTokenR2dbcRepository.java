package com.aetheri.domain.adapter.out.r2dbc;

import com.aetheri.infrastructure.persistence.KakaoToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * KakaoToken 엔티티를 데이터베이스에서 관리하기 위한 레포지토리
 * */
public interface KakaoTokenR2dbcRepository extends R2dbcRepository<KakaoToken, Long> {
    Mono<KakaoToken> findByRunnerId(Long runnerId);
    Mono<Void> deleteByRunnerId(Long runnerId);
    Mono<Boolean> existsByRunnerId(Long runnerId);
}