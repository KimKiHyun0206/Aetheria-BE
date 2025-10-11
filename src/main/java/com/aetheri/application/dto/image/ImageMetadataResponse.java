package com.aetheri.application.dto.image;

import java.time.LocalDate;

/**
 * 이미지 메타데이터 응답을 위한 레코드입니다.
 * 이 레코드는 이미지에 대한 제목, 설명, 위치, 파일 경로, 생성일 및 수정일을 포함합니다.
 *
 * @param title 이미지의 제목입니다.
 * @param description 이미지에 대한 간략한 설명입니다.
 * @param location 이미지가 생성된 위치에 대한 정보입니다.
 * @param imagePath 이미지 파일의 경로입니다.
 * @param createdAt 이미지가 생성된 날짜입니다.
 * @param modifiedAt 이미지가 마지막으로 수정된 날짜입니다.
 */
public record ImageMetadataResponse(
        String title,
        String description,
        String location,
        String imagePath,
        LocalDate createdAt,
        LocalDate modifiedAt
) {
}