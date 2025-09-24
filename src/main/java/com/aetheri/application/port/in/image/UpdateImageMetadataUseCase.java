package com.aetheri.application.port.in.image;

import com.aetheri.application.dto.image.ImageMetadataUpdateRequest;
import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터 수정 유즈케이스
 * */
public interface UpdateImageMetadataUseCase {
    /**
 * Updates metadata for an existing image.
 *
 * <p>Applies the changes provided in {@code request} to the image metadata identified by
 * {@code imageId} only if {@code runnerId} is the owner of that metadata.
 *
 * @param runnerId ID of the user requesting the update; must match the image metadata owner for the update to be applied
 * @param imageId ID of the image metadata to update
 * @param request DTO containing the metadata fields to update
 * @return a {@code Mono<Void>} that completes when the update has been applied (no value emitted)
 */
    Mono<Void> updateImageMetadata(Long runnerId, Long imageId, ImageMetadataUpdateRequest request);
}