package com.aetheri.infrastructure.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("runner")
@NoArgsConstructor
public class Runner {
    @Id
    @Column("id")
    private Long id;

    @Column("kakao_id")
    private Long kakaoId;

    @Column("name")
    private String name;

    public Runner(Long kakaoId, String name) {
        this.kakaoId = kakaoId;
        this.name = name;
    }
}