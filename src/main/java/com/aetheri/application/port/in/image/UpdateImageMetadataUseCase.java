package com.aetheri.application.port.in.image;

import com.aetheri.application.dto.image.UpdateImageMetadataRequest;
import reactor.core.publisher.Mono;

public interface UpdateImageMetadataUseCase {
    Mono<Long> update(UpdateImageMetadataRequest request);
}