package com.aetheri.application.service.image;

import com.aetheri.application.port.in.image.DeleteImageMetadataUseCase;
import com.aetheri.domain.adapter.out.r2dbc.ImageMetadataRepositoryR2dbcAdapter;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteImageMetadataService implements DeleteImageMetadataUseCase {
    private final ImageMetadataRepositoryR2dbcAdapter imageMetadataRepositoryR2DbcAdapter;

    @Override
    public Mono<Void> deleteImageMetadata(Long runnerId, Long imageId) {
        return imageMetadataRepositoryR2DbcAdapter.deleteById(runnerId, imageId)
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