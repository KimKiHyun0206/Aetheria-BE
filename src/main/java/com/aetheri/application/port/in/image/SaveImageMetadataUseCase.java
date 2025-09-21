package com.aetheri.application.port.in.image;

import com.aetheri.application.dto.image.ImageMetadataSaveRequest;
import reactor.core.publisher.Mono;

/**
 * 이미지 생성 요청 포트
 * */
public interface SaveImageMetadataUseCase {
    Mono<Void> generateImage(Long runnerId, ImageMetadataSaveRequest request);
}