package com.aetheri.domain.adapter.out.r2dbc;

import com.aetheri.application.port.out.r2dbc.RunnerRepositoryPort;
import com.aetheri.domain.adapter.out.r2dbc.spi.RunnerR2dbcRepository;
import com.aetheri.infrastructure.persistence.entity.Runner;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Runner 엔티티를 데이터베이스에서 관리하기 위한 어댑터
 *
 * @see RunnerRepositoryPort
 */
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
    public Mono<Boolean> existsByKakaoId(Long kakaoId) {
        return repository.existsByKakaoId(kakaoId);
    }

    @Override
    public Mono<Runner> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Void> deleteByKakaoId(Long kakaoId) {
        return repository.deleteByKakaoId(kakaoId);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
}