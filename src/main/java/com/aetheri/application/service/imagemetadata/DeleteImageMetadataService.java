package com.aetheri.application.service.imagemetadata;

import com.aetheri.application.port.in.imagemetadata.DeleteImageMetadataUseCase;
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
     * Deletes image metadata owned by the specified runner.
     *
     * Attempts to delete the metadata identified by {@code imageId} for the given {@code runnerId}.
     * If no rows are deleted, the reactive stream terminates with a {@link BusinessException} indicating
     * the metadata was not found; otherwise the returned Mono completes empty on success.
     *
     * @param runnerId ID of the user requesting the deletion
     * @param imageId ID of the image metadata to delete
     * @return a Mono that completes empty when deletion succeeds
     * @throws BusinessException when no metadata is found for the given runnerId and imageId
     */
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