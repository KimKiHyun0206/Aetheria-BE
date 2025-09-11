package com.aetheri.infrastructure.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("runner")
@NoArgsConstructor
public class Runner {
    @Id
    private Long id;
    private Long kakaoId;
    private String name;

    public Runner(Long kakaoId, String name) {
        this.kakaoId = kakaoId;
        this.name = name;
    }
}