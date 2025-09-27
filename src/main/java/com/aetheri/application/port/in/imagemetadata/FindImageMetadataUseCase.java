package com.aetheri.application.port.in.imagemetadata;

import com.aetheri.application.dto.image.ImageMetadataResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터 조회 유즈케이스
 */
public interface FindImageMetadataUseCase {
    /**
 * Retrieve image metadata by image ID for a specific requesting user.
 *
 * <p>Returns the metadata only if the requester (runnerId) is the owner of the image.
 *
 * @param runnerId ID of the user requesting the metadata (used for ownership check)
 * @param imageId  ID of the image metadata to retrieve
 * @return a Mono emitting the ImageMetadataResponse when the requester is the image owner
 */
    Mono<ImageMetadataResponse> findImageMetadataById(Long runnerId, Long imageId);

    /**
 * 이미지 ID로 이미지 메타데이터를 조회한다.
 *
 * <p>인증 없이 호출할 수 있으나, 해당 메타데이터가 공유되지 않은 경우 조회가 허용되지 않는다.</p>
 *
 * @param imageId 조회할 이미지 메타데이터의 ID
 * @return 이미지 메타데이터를 담은 {@code Mono<ImageMetadataResponse>}
 * @implSpec 요청자 인증 없이 호출 가능하되, 메타데이터의 공개/공유 상태에 따라 조회 가능 여부가 결정된다.
 */
    Mono<ImageMetadataResponse> findImageMetadataById(Long imageId);

    /**
 * Retrieve all image metadata owned by the specified runner.
 *
 * Returns a stream of ImageMetadataResponse DTOs for the given runner ID.
 *
 * @param runnerId ID of the runner whose image metadata will be returned
 * @return a Flux that emits ImageMetadataResponse objects owned by the runner
 * @implSpec The results are returned as a Reactor Flux and are intended to be delivered to clients via Server-Sent Events (SSE).
 */
    Flux<ImageMetadataResponse> findImageMetadataByRunnerId(Long runnerId);
}