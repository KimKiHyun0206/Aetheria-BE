package com.aetheri.application.service.image;

import com.aetheri.application.dto.image.ImageMetadataSaveRequest;
import com.aetheri.application.dto.image.ImageMetadataSaveDto;
import com.aetheri.application.port.in.image.SaveImageMetadataUseCase;
import com.aetheri.application.port.out.image.ImageRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터를 생성하기 위한 서비스
 *
 * @see SaveImageMetadataUseCase 구현하는 유즈케이스
 * @see ImageRepositoryPort 데이터베이스에 접근하기 위해 접근하는 포트
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaveImageMetadataService implements SaveImageMetadataUseCase {
    private final ImageRepositoryPort imageRepositoryPort;

    /**
     * 이미지 메타데이터를 생성하기 위한 메소드
     * @implSpec 받아오지 않는 값은 자동으로 기본적인 값을 가진 항목들이다.
     * @param runnerId 이미지 메타데이터를 생성한 사용자의 ID
     * @param request 이미지 메타데이터 생성 요청
     * @return 아무런 정보도 응답하지 않는다.
     * */
    @Override
    public Mono<Void> saveImageMetadata(Long runnerId, ImageMetadataSaveRequest request) {
        var imageMetadataSaveDto = new ImageMetadataSaveDto(
                runnerId,
                request.location(),
                request.shape(),
                request.proficiency()
        );

        return imageRepositoryPort.saveImageMetadata(imageMetadataSaveDto)
                .doOnSuccess(l -> log.info("[SaveImageMetadataService] 사용자 {}가 이미지 {}를 생성했습니다.", runnerId, l))
                .then();
    }
}