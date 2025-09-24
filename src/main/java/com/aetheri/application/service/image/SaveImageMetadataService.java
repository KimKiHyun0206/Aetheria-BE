package com.aetheri.application.service.image;

import com.aetheri.application.dto.image.ImageMetadataSaveRequest;
import com.aetheri.application.dto.image.ImageMetadataSaveDto;
import com.aetheri.application.port.in.image.SaveImageMetadataUseCase;
import com.aetheri.application.port.out.image.ImageRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveImageMetadataService implements SaveImageMetadataUseCase {
    private final ImageRepositoryPort imageRepositoryPort;

    @Override
    public Mono<Void> saveImageMetadata(Long runnerId, ImageMetadataSaveRequest request) {
        var imageMetadataSaveDto = new ImageMetadataSaveDto(runnerId, request.location(), request.shape(), request.proficiency());
        return imageRepositoryPort.saveImageMetadata(imageMetadataSaveDto)
                .doOnSuccess(l -> log.info("[SaveImageMetadataService] 사용자 {}가 이미지 {}를 생성했습니다.", runnerId, l))
                .then();
    }
}