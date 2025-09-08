package com.aetheri.domain.adapter.out.r2dbc;

import com.aetheri.infrastructure.persistence.Runner;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface RunnerR2dbcRepository extends R2dbcRepository<Runner, Long> {
    Mono<Runner> findByKakaoId(Long kakaoId);
    Mono<Boolean> existsByKakaoId(Long kakaoId);
}