package com.aetheri.domain.adapter.out.r2dbc;

import com.aetheri.application.port.out.r2dbc.KakaoTokenRepositortyPort;
import com.aetheri.infrastructure.persistence.KakaoToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class KakaoTokenRepositoryR2dbcAdapter implements KakaoTokenRepositortyPort {

    private final KakaoTokenR2dbcRepository repository;

    @Override
    public Mono<KakaoToken> findByRunnerId(Long runnerId) {
        return repository.findByRunnerId(runnerId);
    }

    @Override
    public Mono<Void> deleteByRunnerId(Long runnerId) {
        return repository.deleteByRunnerId(runnerId);
    }

    @Override
    public Mono<Boolean> existByRunnerId(Long runnerId) {
        return repository.existsByRunnerId(runnerId);
    }
}
