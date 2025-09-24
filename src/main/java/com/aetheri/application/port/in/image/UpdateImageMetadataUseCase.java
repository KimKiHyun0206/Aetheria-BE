package com.aetheri.application.port.in.image;

import com.aetheri.application.dto.image.ImageMetadataUpdateRequest;
import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터 수정 유즈케이스
 * */
public interface UpdateImageMetadataUseCase {
    /**
     * 이미지 메타데이터를 수정하기 위한 메소드
     *
     * @param runnerId 수정을 요청한 사용자의 ID
     * @param imageId 수정될 이미지 메타데이터의 ID
     * @param request 수정할 정보가 담긴 DTO
     * @implSpec 만약 사용자의 ID와 이미지 메타데이터의 소유자 ID가 다르면 수정할 수 없어야 한다.
     * @return 수정된 정보를 반환하지 않기에 Void를 반환한다.
     * */
    Mono<Void> updateImageMetadata(Long runnerId, Long imageId, ImageMetadataUpdateRequest request);
}