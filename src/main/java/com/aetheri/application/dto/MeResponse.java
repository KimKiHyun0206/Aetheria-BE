package com.aetheri.application.dto;

import com.aetheri.infrastructure.persistence.entity.Runner;

public record MeResponse(String name) {
    public static MeResponse of(Runner runner) {
        return new MeResponse(runner.getName());
    }
}