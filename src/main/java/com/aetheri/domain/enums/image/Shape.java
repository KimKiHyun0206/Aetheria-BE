package com.aetheri.domain.enums.image;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Shape {
    CIRCLE("원형"),
    SQUARE("사각형"),
    TRIANGLE("삼각형"),
    HEXAGON("육각형");

    private final String description;
}