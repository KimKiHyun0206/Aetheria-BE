package com.aetheri.application.dto.image;

import java.time.LocalDate;

public record ImageMetadataResponse(
        String title,
        String description,
        String location,
        String imagePath,
        LocalDate createdAt,
        LocalDate modifiedAt
) {
}