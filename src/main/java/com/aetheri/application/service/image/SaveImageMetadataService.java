package com.aetheri.application.service.image;

import com.aetheri.application.dto.image.ImageMetadataSaveRequest;
import com.aetheri.application.dto.image.ImageMetadataSaveDto;
import com.aetheri.application.port.in.image.SaveImageMetadataUseCase;
import com.aetheri.domain.adapter.out.r2dbc.ImageMetadataRepositoryR2dbcAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveImageMetadataService implements SaveImageMetadataUseCase {
    private final ImageMetadataRepositoryR2dbcAdapter imageMetadataRepositoryR2dbcAdapter;

    @Override
    public Mono<Void> generateImage(Long runnerId, ImageMetadataSaveRequest request) {
        var imageMetadataSaveDto = new ImageMetadataSaveDto(runnerId, request.location(), request.shape(), request.proficiency());
        return imageMetadataRepositoryR2dbcAdapter.saveImageMetadata(imageMetadataSaveDto)
                .doOnSuccess(l -> log.info("[SaveImageMetadataService] 사용자 {}가 이미지 {}개를 생성했습니다.", runnerId, l))
                .then();
    }
}