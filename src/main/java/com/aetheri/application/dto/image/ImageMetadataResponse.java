package com.aetheri.application.dto.image;

public record ImageMetadataResponse(
        String title,
        String description,
        String location,
        String createdAt,
        String modifiedAt
) {
}