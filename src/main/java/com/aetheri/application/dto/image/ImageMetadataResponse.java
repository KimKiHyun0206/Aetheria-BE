package com.aetheri.application.dto.image;

import java.time.LocalDate;

public record ImageMetadataResponse(
        String title,
        String description,
        String location,
        LocalDate createdAt,
        LocalDate modifiedAt
) {
}