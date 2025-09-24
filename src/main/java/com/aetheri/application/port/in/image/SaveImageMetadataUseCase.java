package com.aetheri.application.port.in.image;

import com.aetheri.application.dto.image.ImageMetadataSaveRequest;
import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터 생성 요청 포트
 * */
public interface SaveImageMetadataUseCase {
    Mono<Void> saveImageMetadata(Long runnerId, ImageMetadataSaveRequest request);
}