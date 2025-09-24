package com.aetheri.application.port.out.image;

import com.aetheri.application.dto.image.ImageMetadataSaveDto;
import com.aetheri.application.dto.image.ImageMetadataUpdateRequest;
import com.aetheri.infrastructure.persistence.entity.ImageMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터를 데이터베이스에서 조작하기 위한 포트
 * */
public interface ImageRepositoryPort {
    /**
 * Persists new image metadata.
 *
 * Persists the metadata described by the provided DTO and emits the generated metadata identifier.
 *
 * @param dto DTO containing the fields required to create the image metadata
 * @return a Mono that emits the generated image metadata ID when persistence completes
 */
    Mono<Long> saveImageMetadata(ImageMetadataSaveDto dto);

    /**
 * Retrieves image metadata by its image identifier.
 *
 * @param imageId the unique identifier of the image whose metadata to retrieve
 * @return a Mono emitting the ImageMetadata if found, or an empty Mono if no matching record exists
 */
    Mono<ImageMetadata> findById(Long imageId);

    /**
 * Retrieves all image metadata records associated with the given runner (user) ID.
 *
 * @param runnerId the runner (user) identifier whose image metadata should be returned
 * @return a reactive stream (Flux) of ImageMetadata for the specified runner; completes empty if none found
 */
    Flux<ImageMetadata> findByRunnerId(Long runnerId);

    /**
 * Updates image metadata for the specified runner and image.
 *
 * Applies the changes provided in the update request to the image metadata identified by imageId and owned by runnerId.
 *
 * @param runnerId the owner/runner identifier used to scope the update
 * @param imageId the identifier of the image metadata to update
 * @param request the update payload containing fields to modify
 * @return a Mono emitting the number of records updated (0 if no matching record was found)
 */
    Mono<Long> updateImageMetadata(Long runnerId, Long imageId, ImageMetadataUpdateRequest request);

    /**
 * Checks whether image metadata exists for the given image ID.
 *
 * @param imageId the ID of the image whose metadata existence is checked
 * @return a Mono emitting {@code true} if metadata exists for the image ID, otherwise {@code false}
 */
    Mono<Boolean> isExistImageMetadata(Long imageId);

    /**
 * Deletes image metadata identified by the given runner (owner) and image IDs.
 *
 * <p>The operation is scoped to the specified runnerId to ensure only metadata owned
 * by that runner is removed.</p>
 *
 * @param runnerId the owner/runner ID that must match the metadata's owner
 * @param imageId  the image metadata identifier to delete
 * @return a Mono emitting the result indicator (commonly the number of records deleted)
 */
    Mono<Long> deleteById(Long runnerId, Long imageId);

    /**
 * Deletes all image metadata records belonging to the specified runner.
 *
 * @param runnerId the ID of the runner whose image metadata should be deleted
 * @return a Mono emitting the number of records deleted
 */
    Mono<Long> deleteByRunnerId(Long runnerId);
}