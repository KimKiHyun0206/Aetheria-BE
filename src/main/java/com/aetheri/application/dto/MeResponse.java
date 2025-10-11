package com.aetheri.application.dto;

import com.aetheri.infrastructure.persistence.entity.Runner;

public record MeResponse(String name) {
    // TODO Domain 엔티티를 직접 참조하고 있는데, 이는 레이어 규칙을 위반한 것임
    public static MeResponse of(Runner runner) {
        return new MeResponse(runner.getName());
    }
}