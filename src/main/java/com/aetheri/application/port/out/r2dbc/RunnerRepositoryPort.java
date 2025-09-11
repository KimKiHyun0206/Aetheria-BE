package com.aetheri.application.port.out.r2dbc;

import com.aetheri.infrastructure.persistence.Runner;
import reactor.core.publisher.Mono;

public interface RunnerRepositoryPort {

    Mono<Runner> findByKakaoId(Long kakaoId);

    Mono<Runner> save(Runner runner);

    Mono<Boolean> existByKakaoId(Long kakaoId);

    Mono<Runner> findById(Long id);

    Mono<Void> deleteByKakaoId(Long kakaoId);

    Mono<Void> deleteById(Long id);
}