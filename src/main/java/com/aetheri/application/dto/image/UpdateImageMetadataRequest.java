package com.aetheri.application.dto.image;

public record UpdateImageMetadataRequest(
        String title,
        String description
) {
}