package com.aetheri.application.port.in.image;

import com.aetheri.application.dto.image.ImageMetadataUpdateRequest;
import reactor.core.publisher.Mono;

public interface UpdateImageMetadataUseCase {
    Mono<Void> updateImageMetadata(Long runnerId, Long imageId, ImageMetadataUpdateRequest request);
}