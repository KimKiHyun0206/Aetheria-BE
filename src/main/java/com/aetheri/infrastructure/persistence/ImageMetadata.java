package com.aetheri.infrastructure.persistence;

import com.aetheri.domain.enums.image.Proficiency;
import com.aetheri.domain.enums.image.Shape;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("image_metadata")
@Getter
@NoArgsConstructor
public class ImageMetadata {
    @Id
    @Column("id")
    private Long id;

    @Column("runner_id")
    private Long runnerId;

    @Column("image_path")
    private String imagePath;

    @Column("description")
    private String description;

    @Column("location")
    private String location;

    @Column("shape")
    private Shape shape;

    @Column("proficiency")
    private Proficiency proficiency;

    @Column("shared")
    private Boolean shared;

    @Builder
    private ImageMetadata(Long runnerId, String imagePath, String description, String location, Shape shape, Proficiency proficiency) {
        this.runnerId = runnerId;
        this.imagePath = imagePath;
        this.description = description;
        this.location = location;
        this.shape = shape;
        this.proficiency = proficiency;
    }

    public static ImageMetadata toEntity(Long runnerId, String location, Shape shape, Proficiency proficiency) {
        return ImageMetadata.builder()
                .runnerId(runnerId)
                .imagePath(runnerId + "-" + UUID.randomUUID())
                .description("설명을 추가해주세요")
                .location(location)
                .shape(shape)
                .proficiency(proficiency)
                .build();
    }
}