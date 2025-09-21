package com.aetheri.application.dto.image;

import com.aetheri.domain.enums.image.Proficiency;
import com.aetheri.domain.enums.image.Shape;
import com.aetheri.infrastructure.persistence.ImageMetadata;

/**
 * 생성된 이미지의 메타데이터를 저장하기 위해 사용하는 DTO.
 * 내부에서만 사용하고 외부에 응답하지 않음.
 */
public record ImageMetadataSaveDto(
        Long runnerId,
        String location,
        Shape shape,
        Proficiency proficiency
) {
    public ImageMetadata toEntity() {
        return ImageMetadata.toEntity(runnerId, location, shape, proficiency);
    }
}