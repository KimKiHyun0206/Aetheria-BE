package com.aetheri.domain.adapter.out.r2dbc;

import com.aetheri.infrastructure.persistence.Runner;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * Runner 엔티티를 데이터베이스에서 관리하기 위한 레포지토리
 */
public interface RunnerR2dbcRepository extends R2dbcRepository<Runner, Long> {
    Mono<Runner> findByKakaoId(Long kakaoId);
    Mono<Boolean> existsByKakaoId(Long kakaoId);
    Mono<Void> deleteByKakaoId(Long kakaoId);
}