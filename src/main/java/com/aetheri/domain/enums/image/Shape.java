package com.aetheri.domain.enums.image;

import lombok.RequiredArgsConstructor;

/**
 * 이미지 또는 객체의 **기본적인 형태(Shape)**를 나타내는 열거형입니다.
 *
 * <p>각 형태는 해당 형태에 대한 한국어 설명을 가집니다.</p>
 */
@RequiredArgsConstructor
public enum Shape {
    /**
     * 원형 형태
     */
    CIRCLE("원형"),
    /**
     * 사각형 형태
     */
    SQUARE("사각형"),
    /**
     * 삼각형 형태
     */
    TRIANGLE("삼각형"),
    /**
     * 육각형 형태
     */
    HEXAGON("육각형");

    /**
     * 해당 형태에 대한 한국어 설명입니다.
     */
    private final String description;
}