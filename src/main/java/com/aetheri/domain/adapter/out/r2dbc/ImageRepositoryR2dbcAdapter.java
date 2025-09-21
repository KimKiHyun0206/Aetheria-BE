package com.aetheri.domain.adapter.out.r2dbc;

import com.aetheri.application.dto.image.ImageMetadataSaveDto;
import com.aetheri.application.dto.image.ImageMetadataUpdateRequest;
import com.aetheri.application.port.out.image.ImageRepositoryPort;
import com.aetheri.domain.adapter.out.r2dbc.spi.ImageR2dbcRepository;
import com.aetheri.infrastructure.persistence.ImageMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryR2dbcAdapter implements ImageRepositoryPort {
    private final ImageR2dbcRepository imageR2dbcRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * 이미지의 메타데이터를 저장하기 위한 메소드.
     *
     * @param dto 등록할 이미지의 메타데이터를 가진 DTO
     * @return 생성이 성공했는지 실패했는지
     */
    public Mono<Long> saveImageMetadata(ImageMetadataSaveDto dto) {
        ImageMetadata entity = ImageMetadata.toEntity(
                dto.runnerId(),
                dto.location(),
                dto.shape(),
                dto.proficiency()
        );
        return imageR2dbcRepository.save(entity)
                .flatMap(this::getImageMetadataId);
    }

    /**
     * 이미지의 PK로 데이터베이스에서 이미지의 메타데이터를 가져오는 메소드.
     */
    public Mono<ImageMetadata> findById(Long imageId) {
        return imageR2dbcRepository.findById(imageId);
    }


    /**
     * 이미지의 runner_id 컬럼을 사용해서 이미지의 메타데이터들을 가져오는 메소드.
     */
    public Flux<ImageMetadata> findByRunnerId(Long runnerId) {
        return imageR2dbcRepository.findAllByRunnerId(runnerId);
    }


    /**
     * 이미지의 메타데이터를 업데이트할 수 있는 메소드
     *
     * @param runnerId 수정을 요청한 사용자의 ID
     * @param imageId  수정할 이미지의 ID
     * @implNote 수정할 이미지 메타데이터의 소유자만 삭제할 수 있도록 구현함.
     */
    public Mono<Long> updateImageMetadata(Long imageId, Long runnerId, ImageMetadataUpdateRequest request) {
        Query query = Query.query(
                Criteria.where("id").is(imageId)
                        .and("runner_id").is(runnerId)
        );

        Update update = Update
                .update("title", request.title())
                .set("description", request.description());

        return r2dbcEntityTemplate.update(query, update, ImageMetadata.class);
    }

    /**
     * 이미지가 존재하는지 확인하기 위한 메소드
     */
    public Mono<Boolean> isExistImageMetadata(Long imageId) {
        return imageR2dbcRepository.existsById(imageId);
    }

    /**
     * 이미지를 삭제하기 위한 메소드
     *
     * @param runnerId 삭제를 요청한 사용자의 ID
     * @param imageId  삭제할 이미지의 ID
     * @implNote 삭제할 이미지 메타데이터의 소유자만 삭제할 수 있도록 구현함.
     */
    public Mono<Boolean> deleteById(Long runnerId, Long imageId) {
        return imageR2dbcRepository.findById(imageId).flatMap(
                imageMetadata -> {
                    if (imageMetadata.getId().equals(runnerId)) {
                        return imageR2dbcRepository.deleteById(imageId)
                                .then(Mono.just(true));
                    } else {
                        return Mono.just(false);
                    }
                }
        ).defaultIfEmpty(false);
    }

    ;

    /**
     * 새로 생성된 이미지의 PK를 가져오기 위한 메소드
     */
    private Mono<Long> getImageMetadataId(ImageMetadata imageMetadata) {
        return Mono.just(imageMetadata.getId());
    }
}