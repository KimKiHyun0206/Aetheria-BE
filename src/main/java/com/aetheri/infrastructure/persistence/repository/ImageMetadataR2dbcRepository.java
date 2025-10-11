package com.aetheri.infrastructure.persistence.repository;

import com.aetheri.infrastructure.persistence.entity.ImageMetadata;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

/**
 * {@code ImageMetadata} 엔티티에 대한 비동기/논블로킹 R2DBC 데이터 접근을 제공하는 Repository 인터페이스입니다.
 *
 * <p>Spring Data R2DBC의 {@link R2dbcRepository}를 상속받아 기본 CRUD 기능을 제공하며,
 * 쿼리 메서드 정의를 통해 특정 조건의 데이터를 조회합니다.</p>
 */
public interface ImageMetadataR2dbcRepository extends R2dbcRepository<ImageMetadata, Long> {

    /**
     * 주어진 사용자 ID({@code runnerId})에 의해 생성된 모든 이미지 메타데이터 목록을 조회합니다.
     *
     * <p>이 메서드는 {@code runner_id} 컬럼을 기준으로 데이터를 찾습니다.</p>
     *
     * @param runnerId 조회할 이미지의 소유자(사용자) 고유 ID입니다.
     * @return 해당 사용자 ID와 일치하는 모든 {@code ImageMetadata} 엔티티를 발행하는 {@code Flux}입니다.
     */
    Flux<ImageMetadata> findAllByRunnerId(Long runnerId);
}