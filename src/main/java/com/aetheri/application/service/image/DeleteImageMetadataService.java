package com.aetheri.application.service.image;

import com.aetheri.application.port.in.image.DeleteImageMetadataUseCase;
import com.aetheri.application.port.out.image.ImageRepositoryPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터를 삭제하기 위한 서비스
 *
 * @see DeleteImageMetadataUseCase 구현하는 유즈케이스
 * @see ImageRepositoryPort 데이터베이스에 접근하기 위해 접근하는 포트
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteImageMetadataService implements DeleteImageMetadataUseCase {
    private final ImageRepositoryPort imageRepositoryPort;

    /**
     * 이미지 메타데이터를 삭제하기 위한 서비스 메소드
     *
     * @param runnerId 이미지 메타데이터 삭제를 요청한 사용자의 ID
     * @param imageId 삭제 요청된 이미지 메타데이터의 ID
     * @implSpec 만약 삭제된 행의 갯수가 0개라면 삭제되지 않았다고 판단하고 에러 응답
     * @exception BusinessException 삿제된 행의 갯수가 0개일 때 에러 반환
     * @return 아무런 정보도 응답하지 않는다.
     * */
    @Override
    public Mono<Void> deleteImageMetadata(Long runnerId, Long imageId) {
        return imageRepositoryPort.deleteById(runnerId, imageId)
                .flatMap(deletedCount -> {
                    if (deletedCount == 0) {
                        return Mono.error(new BusinessException(
                                ErrorMessage.NOT_FOUND_IMAGE_METADATA,
                                "요청한 이미지 메타데이터를 찾지 못해 삭제하지 못했습니다."
                        ));
                    }
                    log.info("[DeleteImageService] 사용자 {}에 의해 이미지 {}개가 삭제되었습니다.", runnerId, deletedCount);
                    return Mono.empty();
                });
    }
}