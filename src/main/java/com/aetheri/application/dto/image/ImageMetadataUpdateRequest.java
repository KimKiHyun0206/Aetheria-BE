package com.aetheri.application.dto.image;

/**
 * 이미지 메타데이터 수정을 요청하는 데 사용되는 레코드입니다.
 * 이 레코드는 이미지의 제목과 설명을 업데이트하기 위한 정보를 담고 있으며,
 * 요청 본문(Request Body)으로 사용됩니다.
 *
 * @param title 이미지의 새로운 제목입니다.
 * @param description 이미지에 대한 새로운 간략한 설명입니다.
 */
public record ImageMetadataUpdateRequest(
        String title,
        String description
) {
}