package com.aetheri.application.dto.image;

import com.aetheri.domain.enums.image.Proficiency;
import com.aetheri.domain.enums.image.Shape;

public record ImageMetadataSaveRequest(
        String location,
        Proficiency proficiency,
        Shape shape
) {
}