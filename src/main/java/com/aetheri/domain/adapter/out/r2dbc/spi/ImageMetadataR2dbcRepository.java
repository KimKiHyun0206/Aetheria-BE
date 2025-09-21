package com.aetheri.domain.adapter.out.r2dbc.spi;

import com.aetheri.infrastructure.persistence.ImageMetadata;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ImageMetadataR2dbcRepository extends R2dbcRepository<ImageMetadata, Long> {
    Flux<ImageMetadata> findAllByRunnerId(Long runnerId);
}
