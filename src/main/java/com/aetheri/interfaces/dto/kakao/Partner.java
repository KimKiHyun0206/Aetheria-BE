package com.aetheri.interfaces.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Partner(
        // 고유 ID
        @JsonProperty("uuid")
        String uuid
) {}