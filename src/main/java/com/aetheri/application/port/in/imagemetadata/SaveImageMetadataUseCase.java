package com.aetheri.application.port.in.imagemetadata;

import com.aetheri.application.dto.image.ImageMetadataSaveRequest;
import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터 생성 요청 포트
 * */
public interface SaveImageMetadataUseCase {
    /**
 * Saves image metadata for the specified runner.
 *
 * Persists the image metadata contained in the given request and associates it with the provided runner identifier.
 *
 * @param runnerId identifier of the runner context to associate the metadata with
 * @param request payload containing the image metadata to save
 * @return a Mono that completes when the metadata has been persisted
 */
Mono<Void> saveImageMetadata(Long runnerId, ImageMetadataSaveRequest request);
}