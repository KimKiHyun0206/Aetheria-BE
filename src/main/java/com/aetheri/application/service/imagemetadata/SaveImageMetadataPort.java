package com.aetheri.application.service.imagemetadata;

import com.aetheri.application.dto.image.ImageMetadataSaveRequest;
import com.aetheri.application.port.in.imagemetadata.SaveImageMetadataUseCase;
import com.aetheri.application.port.out.image.ImageRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터를 생성하기 위한 서비스
 *
 * @see SaveImageMetadataUseCase 구현하는 유즈케이스
 * @see ImageRepositoryPort 데이터베이스에 접근하기 위해 접근하는 포트
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaveImageMetadataPort implements SaveImageMetadataUseCase {
    private final ImageRepositoryPort imageRepositoryPort;

    /**
     * Persist image metadata for the given runner.
     *
     * Builds an ImageMetadataSaveDto from the provided runnerId and request and delegates persistence
     * to the image repository port. The returned Mono completes when the save operation succeeds;
     * any errors from the repository are propagated downstream.
     *
     * @param runnerId the ID of the user creating the image metadata
     * @param request  request containing metadata fields (location, shape, proficiency)
     * @return a Mono that completes when the metadata has been persisted
     */
    @Override
    public Mono<Void> saveImageMetadata(Long runnerId, ImageMetadataSaveRequest request) {
        return imageRepositoryPort.saveImageMetadata(runnerId, request)
                .doOnSuccess(l -> log.info("[SaveImageMetadataService] 사용자 {}가 이미지 {}를 생성했습니다.", runnerId, l))
                .then();
    }
}