package com.aetheri.domain.enums.image;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Proficiency {
    BEGINNER(5, "초보자"),
    INTERMEDIATE(10, "중급자"),
    ADVANCED(21, "숙련자"),
    EXPERT(42, "전문가");

    private final int distanceInKm;
    private final String description;
}