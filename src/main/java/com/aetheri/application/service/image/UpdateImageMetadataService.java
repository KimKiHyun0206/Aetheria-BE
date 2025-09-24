package com.aetheri.application.service.image;

import com.aetheri.application.dto.image.ImageMetadataUpdateRequest;
import com.aetheri.application.port.in.image.UpdateImageMetadataUseCase;
import com.aetheri.application.port.out.image.ImageRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateImageMetadataService implements UpdateImageMetadataUseCase {
    private final ImageRepositoryPort imageRepositoryPort;

    @Override
    public Mono<Void> updateImageMetadata(Long runnerId, Long imageId, ImageMetadataUpdateRequest request) {
        return imageRepositoryPort.updateImageMetadata(runnerId, imageId, request)
                .doOnSuccess(l -> log.info("[UpdateImageMetadataService] 사용자 {}가 이미지 {}의 메타데이터를 수정했습니다. 바뀐 행: {}", runnerId, imageId, l))
                .then();
    }
}