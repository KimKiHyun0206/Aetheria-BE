package com.aetheri.application.port.out.image;

import com.aetheri.application.dto.image.ImageMetadataSaveDto;
import com.aetheri.application.dto.image.ImageMetadataUpdateRequest;
import com.aetheri.infrastructure.persistence.entity.ImageMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ImageRepositoryPort {
    Mono<Long> saveImageMetadata(ImageMetadataSaveDto dto);
    Mono<ImageMetadata> findById(Long imageId);
    Flux<ImageMetadata> findByRunnerId(Long runnerId);
    Mono<Long> updateImageMetadata(Long runnerId, Long imageId, ImageMetadataUpdateRequest request);
    Mono<Boolean> isExistImageMetadata(Long imageId);
    Mono<Long> deleteById(Long runnerId, Long imageId);
    Mono<Long> deleteByRunnerId(Long runnerId);
}