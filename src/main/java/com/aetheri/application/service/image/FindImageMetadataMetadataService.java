package com.aetheri.application.service.image;

import com.aetheri.application.dto.image.ImageMetadataResponse;
import com.aetheri.application.port.in.image.FindImageMetadataUseCase;
import com.aetheri.domain.adapter.out.r2dbc.ImageMetadataRepositoryR2dbcAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindImageMetadataMetadataService implements FindImageMetadataUseCase {
    private final ImageMetadataRepositoryR2dbcAdapter imageMetadataRepositoryR2DbcAdapter;

    @Override
    public Mono<ImageMetadataResponse> findImageMetadataById(Long imageId) {
        return imageMetadataRepositoryR2DbcAdapter.findById(imageId).map(imageMetadata -> {
            log.info("[FindImageService] 이미지 {}를 조회했습니다.", imageId);
            return imageMetadata.toResponse();
        });
    }

    @Override
    public Flux<ImageMetadataResponse> findImageMetadataByRunnerId(Long runnerId) {
        return imageMetadataRepositoryR2DbcAdapter.findByRunnerId(runnerId).map(imageMetadata -> {
            log.info("[FindImageService] 사용자 {}의 이미지를 조회했습니다.", runnerId);
            return imageMetadata.toResponse();
        });
    }
}