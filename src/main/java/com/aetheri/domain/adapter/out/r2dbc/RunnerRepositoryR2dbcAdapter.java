package com.aetheri.domain.adapter.out.r2dbc;

import com.aetheri.application.port.out.r2dbc.RunnerRepositoryPort;
import com.aetheri.infrastructure.persistence.Runner;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RunnerRepositoryR2dbcAdapter implements RunnerRepositoryPort {

    private final RunnerR2dbcRepository repository;

    public RunnerRepositoryR2dbcAdapter(RunnerR2dbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Runner> findByKakaoId(Long kakaoId) {
        return repository.findByKakaoId(kakaoId);
    }

    @Override
    public Mono<Runner> save(Runner runner) {
        return repository.save(runner);
    }

    @Override
    public Mono<Boolean> existByKakaoId(Long kakaoId) {
        return repository.existsRunnerByKakaoId(kakaoId);
    }
}