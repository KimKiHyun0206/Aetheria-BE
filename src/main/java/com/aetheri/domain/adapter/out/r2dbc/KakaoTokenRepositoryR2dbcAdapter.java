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
    public Mono<Void> save(Long runnerId, String accessToken, String refreshToken) {
        return repository.existsByRunnerId(runnerId)
                .flatMap(exists -> {
                    Mono<KakaoToken> saveMono = repository.save(
                            KakaoToken.toEntity(runnerId, accessToken, refreshToken)
                    );
                    if (exists) {
                        // 기존 토큰 삭제 후 새로 저장
                        return repository.deleteByRunnerId(runnerId)
                                .then(saveMono)
                                .then();
                    } else {
                        // 존재하지 않으면 바로 저장
                        return saveMono.then();
                    }
                });
    }

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
