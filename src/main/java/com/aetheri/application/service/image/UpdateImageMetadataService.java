package com.aetheri.application.service.image;

import com.aetheri.application.dto.image.ImageMetadataUpdateRequest;
import com.aetheri.application.port.in.image.UpdateImageMetadataUseCase;
import com.aetheri.application.port.out.image.ImageRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터를 수정하기 위한 서비스
 *
 * @see UpdateImageMetadataUseCase 구현하는 유즈케이스
 * @see ImageRepositoryPort 데이터베이스에 접근하기 위해 접근하는 포트
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateImageMetadataService implements UpdateImageMetadataUseCase {
    private final ImageRepositoryPort imageRepositoryPort;

    /**
     * Update metadata for an image.
     *
     * <p>Only the owner of the image (the requesting user) is allowed to perform this update;
     * this method delegates authorization/enforcement to the underlying repository/port.
     *
     * @param runnerId the ID of the user requesting the update
     * @param imageId  the ID of the image whose metadata will be updated
     * @param request  the metadata update payload
     * @return a Mono that completes when the update finishes; it emits no value
     *         (errors from the repository call propagate through the returned Mono)
     */
    @Override
    public Mono<Void> updateImageMetadata(Long runnerId, Long imageId, ImageMetadataUpdateRequest request) {
        return imageRepositoryPort.updateImageMetadata(runnerId, imageId, request)
                .doOnSuccess(l -> log.info("[UpdateImageMetadataService] 사용자 {}가 이미지 {}의 메타데이터를 수정했습니다. 바뀐 행: {}", runnerId, imageId, l))
                .then();
    }
}