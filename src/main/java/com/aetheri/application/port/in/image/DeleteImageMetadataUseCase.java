package com.aetheri.application.port.in.image;

import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터 삭제 유즈케이스
 * */
public interface DeleteImageMetadataUseCase {
    /**
 * Delete image metadata for a given user.
 *
 * <p>Deletes the metadata for the image identified by {@code imageId} on behalf of the user
 * identified by {@code runnerId}. The returned {@code Mono} completes when the deletion
 * operation has finished.</p>
 *
 * @param runnerId ID of the user requesting the deletion
 * @param imageId  ID of the image whose metadata should be deleted
 * @return a {@code Mono<Void>} that completes when the metadata deletion is finished
 */
    Mono<Void> deleteImageMetadata(Long runnerId, Long imageId);
}