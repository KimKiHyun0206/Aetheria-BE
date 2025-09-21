package com.aetheri.domain.adapter.out.r2dbc;

import com.aetheri.application.dto.image.ImageMetadataSaveDto;
import com.aetheri.application.port.out.image.ImageRepositoryPort;
import com.aetheri.domain.adapter.out.r2dbc.spi.ImageR2dbcRepository;
import com.aetheri.infrastructure.persistence.ImageMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryR2dbcAdapter implements ImageRepositoryPort {
    private final ImageR2dbcRepository imageR2dbcRepository;

    /**
     * 이미지의 메타데이터를 저장하기 위한 메소드.
     * */
    public Mono<Void> saveImage(ImageMetadataSaveDto dto) {
        ImageMetadata entity = dto.toEntity();
        return imageR2dbcRepository.save(entity).then();
    }

    /**
     * 이미지의 PK로 데이터베이스에서 이미지의 메타데이터를 가져오는 메소드.
     */
    public Mono<ImageMetadata> findByImageId(Long imageId) {
        return imageR2dbcRepository.findById(imageId);
    }


    /**
     * 이미지의 runner_id 컬럼을 사용해서 이미지의 메타데이터들을 가져오는 메소드.
     */
    public Flux<ImageMetadata> findByRunnerId(Long runnerId) {
        return imageR2dbcRepository.findAllByRunnerId(runnerId);
    }
}