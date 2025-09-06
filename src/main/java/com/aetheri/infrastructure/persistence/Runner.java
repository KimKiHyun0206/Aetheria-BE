package com.aetheri.infrastructure.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("runner")
@NoArgsConstructor
public class Runner {
    private Long id;
    private Long kakaoId;
    private String name;

    public Runner(Long kakaoId, String name) {
        this.kakaoId = kakaoId;
        this.name = name;
    }
}