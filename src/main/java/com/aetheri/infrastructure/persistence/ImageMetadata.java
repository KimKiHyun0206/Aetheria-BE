package com.aetheri.infrastructure.persistence;

import com.aetheri.application.dto.image.ImageMetadataResponse;
import com.aetheri.domain.enums.image.Proficiency;
import com.aetheri.domain.enums.image.Shape;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
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

    @Column("title")
    private String title;

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

    @Column("created_at")
    private LocalDate createdAt;

    @Column("modified_at")
    private LocalDate modifiedAt;

    @Builder
    public ImageMetadata(Long runnerId, String imagePath, String title, String description, String location, Shape shape, Proficiency proficiency, Boolean shared, LocalDate createdAt, LocalDate modifiedAt) {
        this.runnerId = runnerId;
        this.imagePath = imagePath;
        this.title = title;
        this.description = description;
        this.location = location;
        this.shape = shape;
        this.proficiency = proficiency;
        this.shared = shared;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static ImageMetadata toEntity(Long runnerId, String location, Shape shape, Proficiency proficiency) {
        return ImageMetadata.builder()
                .runnerId(runnerId)
                .imagePath(runnerId + "-" + UUID.randomUUID())
                .title(location + " " + shape.name())
                .description("설명을 추가해주세요")
                .location(location)
                .shape(shape)
                .proficiency(proficiency)
                .createdAt(LocalDate.now())
                .modifiedAt(LocalDate.now())
                .build();
    }

    public ImageMetadataResponse toResponse(){
        return new ImageMetadataResponse(
                this.title,
                this.description,
                this.location,
                this.createdAt.toString(),
                this.modifiedAt.toString()
        );
    }
}