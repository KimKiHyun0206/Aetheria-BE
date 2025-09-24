package com.aetheri.application.port.in.image;

import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터 삭제 유즈케이스
 * */
public interface DeleteImageMetadataUseCase {
    /**
     * 이미지 메타데이터를 삭제할 수 있는 메소드
     *
     * @param imageId 삭제할 이미지의 ID
     * @param runnerId 이미지를 삭제할 사용자의 ID
     * @return 삭제하므로 Void를 리턴한다
     * */
    Mono<Void> deleteImageMetadata(Long runnerId, Long imageId);
}