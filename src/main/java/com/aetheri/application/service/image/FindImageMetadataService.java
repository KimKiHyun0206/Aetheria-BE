package com.aetheri.application.service.image;

import com.aetheri.application.dto.image.ImageMetadataResponse;
import com.aetheri.application.port.in.image.FindImageMetadataUseCase;
import com.aetheri.application.port.out.image.ImageRepositoryPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.persistence.entity.ImageMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindImageMetadataService implements FindImageMetadataUseCase {
    private final ImageRepositoryPort imageRepositoryPort;

    @Override
    public Mono<ImageMetadataResponse> findImageMetadataById(Long runnerId, Long imageId) {
        return imageRepositoryPort.findById(imageId)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorMessage.NOT_FOUND_IMAGE_METADATA, "이미지를 찾을 수 없습니다.")))
                .flatMap(imageMetadata -> {
                    log.info("[FindImageMetadataMetadataService] 사용자 {}가 이미지 {}를 조회했습니다.", runnerId, imageId);
                    if (imageMetadata.getShared() || imageMetadata.getRunnerId().equals(runnerId)) {
                        return Mono.just(imageMetadata.toResponse());
                    } else {
                        return Mono.error(
                                new BusinessException(
                                        ErrorMessage.RUNNER_IS_NOT_OWNER_OF_IMAGE_METADATA,
                                        "요청자는 이미지의 주인이 아닙니다."
                                )
                        );
                    }
                });
    }

    @Override
    public Mono<ImageMetadataResponse> findImageMetadataById(Long imageId) {
        return imageRepositoryPort.findById(imageId)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorMessage.NOT_FOUND_IMAGE_METADATA, "이미지를 찾을 수 없습니다.")))
                .flatMap(imageMetadata -> {
                    log.info("[FindImageMetadataMetadataService] 이미지 {}를 조회했습니다.", imageId);
                    if (imageMetadata.getShared()) {
                        return Mono.just(imageMetadata.toResponse());
                    } else {
                        return Mono.error(
                                new BusinessException(
                                        ErrorMessage.RUNNER_IS_NOT_OWNER_OF_IMAGE_METADATA,
                                        "요청자는 이미지의 주인이 아닙니다."
                                )
                        );
                    }
                });
    }

    @Override
    public Flux<ImageMetadataResponse> findImageMetadataByRunnerId(Long runnerId) {
        return imageRepositoryPort.findByRunnerId(runnerId).map(ImageMetadata::toResponse)
                .doOnComplete(() -> log.info("[FindImageMetadataMetadataService] 사용자 {}의 이미지를 조회했습니다.", runnerId));
    }
}