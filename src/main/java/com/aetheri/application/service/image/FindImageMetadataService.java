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

/**
 * 이미지 메타데이터를 조회하기 위한 서비스
 *
 * @see FindImageMetadataService 구현하는 유즈케이스
 * @see ImageRepositoryPort 데이터베이스에 접근하기 위해 접근하는 포트
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class FindImageMetadataService implements FindImageMetadataUseCase {
    private final ImageRepositoryPort imageRepositoryPort;

    /**
     * 이미지 메타데이터를 조회하기 위한 메소드
     *
     * @implSpec 사용자 ID를 포함한 요청일 때 이 메소드를 사용한다.
     * @param runnerId 이미지 메타데이터 조회를 요청한 사용자의 ID
     * @param imageId 조회 요청된 이미지 메타데이터의 ID
     * @return 이미지 메타데이터를 응답하는 DTO
     * @exception BusinessException 이미지 메타데이터를 찾지 못했을 때 에러 반환
     * */
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

    /**
     * 이미지 메타데이터를 조회하기 위한 메소드
     *
     * @implSpec 사용자 ID를 포함하지 않은 요청일 때 이 메소드를 사용한다.
     * @param imageId 조회 요청된 이미지 메타데이터의 ID
     * @return 이미지 메타데이터를 응답하는 DTO
     * @exception BusinessException 이미지 메타데이터를 찾지 못했을 때 에러 반환
     * */
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