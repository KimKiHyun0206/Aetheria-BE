package com.aetheri.infrastructure.persistence;

import com.aetheri.domain.enums.image.Proficiency;
import com.aetheri.domain.enums.image.Shape;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("image")
@NoArgsConstructor
public class Image {
    @Id
    @Column("id")
    private Long id;

    @Column("runner_id")
    private Long runnerId;

    @Column("storage_path")
    private String storagePath;

    @Column("description")
    private String description;

    @Column("location")
    private String location;

    @Column("shape")
    private Shape shape;

    @Column("proficiency")
    private Proficiency proficiency;

    @Builder
    private Image(Long runnerId, String storagePath, String description, String location, Shape shape, Proficiency proficiency) {
        this.runnerId = runnerId;
        this.storagePath = storagePath;
        this.description = description;
        this.location = location;
        this.shape = shape;
        this.proficiency = proficiency;
    }

    public static Image toEntity(Long runnerId, String storagePath, String description, String location, Shape shape, Proficiency proficiency) {
        return Image.builder()
                .runnerId(runnerId)
                .storagePath(storagePath)
                .description(description)
                .location(location)
                .shape(shape)
                .proficiency(proficiency)
                .build();
    }
}