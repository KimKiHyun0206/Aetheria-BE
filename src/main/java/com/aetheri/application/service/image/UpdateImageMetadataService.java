package com.aetheri.application.service.image;

import com.aetheri.application.dto.image.ImageMetadataUpdateRequest;
import com.aetheri.application.port.in.image.UpdateImageMetadataUseCase;
import com.aetheri.application.port.out.image.ImageRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터를 수정하기 위한 서비스
 *
 * @see UpdateImageMetadataUseCase 구현하는 유즈케이스
 * @see ImageRepositoryPort 데이터베이스에 접근하기 위해 접근하는 포트
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateImageMetadataService implements UpdateImageMetadataUseCase {
    private final ImageRepositoryPort imageRepositoryPort;

    /**
     * 이미지 메타데이터를 수정하기 위한 메소드
     * @implSpec 사용자의 것이 아니면 수정할 수 없도록 두현함
     * @param runnerId 수정을 요천한 사용자의 ID
     * @param imageId 수정 요청된 이미지 메타데이터의 ID
     * @param request 수정 요청
     * @return 아무런 정보도 응답하지 않는다.
     * */
    @Override
    public Mono<Void> updateImageMetadata(Long runnerId, Long imageId, ImageMetadataUpdateRequest request) {
        return imageRepositoryPort.updateImageMetadata(runnerId, imageId, request)
                .doOnSuccess(l -> log.info("[UpdateImageMetadataService] 사용자 {}가 이미지 {}의 메타데이터를 수정했습니다. 바뀐 행: {}", runnerId, imageId, l))
                .then();
    }
}