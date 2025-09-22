package com.aetheri.infrastructure.persistence.repository;

import com.aetheri.infrastructure.persistence.entity.ImageMetadata;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ImageMetadataR2dbcRepository extends R2dbcRepository<ImageMetadata, Long> {
    Flux<ImageMetadata> findAllByRunnerId(Long runnerId);
}
