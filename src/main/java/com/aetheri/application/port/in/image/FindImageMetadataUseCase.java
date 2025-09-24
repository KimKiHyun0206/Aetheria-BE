package com.aetheri.application.port.in.image;

import com.aetheri.application.dto.image.ImageMetadataResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터 조회 유즈케이스
 */
public interface FindImageMetadataUseCase {
    /**
     * 사용자의 ID와 이미지의 ID로 메타데이터를 찾을 수 있는 메소드
     *
     * @param imageId  조회할 이미지 메타데이터의 ID
     * @param runnerId 조회를 요청한 사용자의 ID
     * @implSpec 요청자가 이미지 메타데이터의 소유자인지 검사한다,
     * @return 이미지 메타데이터 정보를 가진 DTO
     */
    Mono<ImageMetadataResponse> findImageMetadataById(Long runnerId, Long imageId);

    /**
     * 이미지의 ID로 메타데이터를 찾을 수 있는 메소드
     *
     * @param imageId 조회할 이미지 메타데이터의 ID
     * @implSpec 요청자의 인증이 없이도 이미지 메타데이터를 조죄할 수 있다.
     *           하지만 이미지 메타데이터의 상태가 공유되지 않았따면 조회할 수 없다.
     * @return 이미지 메타데이터의 정보를 가진 DTO
     */
    Mono<ImageMetadataResponse> findImageMetadataById(Long imageId);

    /**
     * 사용자의 ID로 이미지 메타데이트롤 찾을 수 있는 메소드
     *
     * @param runnerId 이미지 메타데이터를 가진 사용자의 ID
     * @implSpec FLux로 반환하고 이는 SSE 로 클라이언트로 응답하게 된다.
     * @return 이미지 메타데이터의 정보를 가진 DTO
     * */
    Flux<ImageMetadataResponse> findImageMetadataByRunnerId(Long runnerId);
}