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
    // 이미지 메타데이터를 등록하기 위한 메소드
    Mono<Long> saveImageMetadata(ImageMetadataSaveDto dto);

    // 이미지 메타데이터를 ID로 조회하기 위한 메소드
    Mono<ImageMetadata> findById(Long imageId);

    // 이미지 메타데이터를 사용자 ID로 조회하기 위한 메소드
    Flux<ImageMetadata> findByRunnerId(Long runnerId);

    // 이미지 메타데이터를 수정하기 위한 메소드
    Mono<Long> updateImageMetadata(Long runnerId, Long imageId, ImageMetadataUpdateRequest request);

    // 이미지 메타데이터가 존재하는지 확인하기 위한 메소드
    Mono<Boolean> isExistImageMetadata(Long imageId);

    // 이미지 메타데이터를 사용자 ID와 이미지 ID를 사용해서 삭제하기 위한 메소드
    Mono<Long> deleteById(Long runnerId, Long imageId);

    // 이미지 메타데이터를 사용자 ID를 사용해서 삭제하기 위한 메소드
    Mono<Long> deleteByRunnerId(Long runnerId);
}