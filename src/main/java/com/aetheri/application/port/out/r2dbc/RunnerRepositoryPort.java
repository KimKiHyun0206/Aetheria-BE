package com.aetheri.application.port.out.r2dbc;

import com.aetheri.infrastructure.persistence.entity.Runner;
import reactor.core.publisher.Mono;

/**
 * 사용자 정보 조회를 위해 데이터베이스에 접근하는 포트.
 *
 * @see com.aetheri.domain.adapter.out.r2dbc.RunnerRepositoryR2dbcAdapter
 */
public interface RunnerRepositoryPort {

    /**
     * 카카오 API에서 알려준 사용자의 카카오 ID를 사용해 조회하는 메소드
     *
     * @param kakaoId 카카오 ID
     */
    Mono<Runner> findByKakaoId(Long kakaoId);

    /**
     * 사용자를 저장하기 위한 메소드.
     *
     * @param runner 저장될 사용자 엔티티
     */
    Mono<Runner> save(Runner runner);

    /**
     * 카카오 ID를 사용하여 사용자가 존재하는지 확인하기 위한 메소드
     *
     * @param kakaoId 카카오 ID
     */
    Mono<Boolean> existsByKakaoId(Long kakaoId);

    /**
     * 사용자의 ID를 통해 사용자를 찾기 위한 메소드
     *
     * @param id 사용자의 ID
     */
    Mono<Runner> findById(Long id);

    /**
     * 카카오 ID를 사용하여 사용자를 삭제하기 위한 메소드
     *
     * @param kakaoId 카카오 ID
     */
    Mono<Void> deleteByKakaoId(Long kakaoId);

    /**
     * 사용자의 ID를 사용하여 사용자를 삭제하기 위한 메소드
     *
     * @param id 사용자의 ID
     */
    Mono<Void> deleteById(Long id);
}