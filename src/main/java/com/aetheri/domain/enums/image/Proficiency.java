package com.aetheri.domain.enums.image;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 이미지 관련 항목 또는 사용자 레벨을 나타내는 **숙련도(Proficiency) 레벨** 열거형입니다.
 *
 * <p>각 숙련도 레벨은 해당 레벨에 도달하기 위해 요구되는 최소 거리(km)와 한국어 설명을 가집니다.</p>
 */
@RequiredArgsConstructor
@Getter
public enum Proficiency {
    /**
     * 초보자 레벨. 최소 요구 거리: 5km
     */
    BEGINNER(5, "초보자"),
    /**
     * 중급자 레벨. 최소 요구 거리: 10km
     */
    INTERMEDIATE(10, "중급자"),
    /**
     * 숙련자 레벨. 최소 요구 거리: 21km (하프 마라톤 기준)
     */
    ADVANCED(21, "숙련자"),
    /**
     * 전문가 레벨. 최소 요구 거리: 42km (풀 마라톤 기준)
     */
    EXPERT(42, "전문가");

    /**
     * 해당 숙련도 레벨에 도달하기 위해 필요한 최소 거리(km)입니다.
     */
    private final int distanceInKm;
    /**
     * 해당 숙련도 레벨에 대한 한국어 설명입니다.
     */
    private final String description;
}