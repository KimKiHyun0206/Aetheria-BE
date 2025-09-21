package com.aetheri.application.port.in.image;

import com.aetheri.application.dto.image.ImageMetadataResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 이미지 조회 유즈케이스
 * */
public interface FindImageMetadataUseCase {
    Mono<ImageMetadataResponse> findImageById(Long imageId);
    Flux<ImageMetadataResponse> findImageByRunnerId(Long runnerId);
}