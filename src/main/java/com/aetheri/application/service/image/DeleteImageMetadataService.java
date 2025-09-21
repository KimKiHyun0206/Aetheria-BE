package com.aetheri.application.service.image;

import com.aetheri.application.port.in.image.DeleteImageMetadataUseCase;
import com.aetheri.domain.adapter.out.r2dbc.ImageMetadataRepositoryR2dbcAdapter;
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
    public Mono<Void> delete(Long runnerId, Long imageId) {
        return imageMetadataRepositoryR2DbcAdapter.deleteById(runnerId, imageId)
                .doOnSuccess(l -> logResult(runnerId, l))
                .then();
    }

    private void logResult(Long runnerId, Long deletedCount) {
        log.info("[DeleteImageService] 사용자 {}에 의해 이미지 {}개가 삭제되었습니다.", runnerId, deletedCount);
    }
}