package com.aetheri.application.port.in.imagemetadata;

import reactor.core.publisher.Mono;

/**
 * 이미지 메타데이터 삭제와 관련된 비즈니스 로직을 정의하는 유즈케이스 인터페이스입니다.
 * 이 인터페이스는 특정 사용자를 대신하여 이미지 메타데이터를 삭제하는 기능을 제공합니다.
 */
public interface DeleteImageMetadataUseCase {
    /**
     * 주어진 사용자가 특정 이미지의 메타데이터를 삭제합니다.
     *
     * <p>이 메서드는 {@code runnerId}로 식별되는 사용자의 요청에 따라 {@code imageId}로 식별되는
     * 이미지의 메타데이터를 삭제하는 책임을 가집니다. 삭제 작업이 완료되면 반환되는 {@code Mono}가 종료됩니다.</p>
     *
     * @param runnerId 삭제를 요청하는 사용자의 고유 식별자(ID)입니다.
     * @param imageId 메타데이터를 삭제할 이미지의 고유 식별자(ID)입니다.
     * @return 메타데이터 삭제 작업이 완료되었을 때 종료되는 {@code Mono<Void>} 객체입니다.
     */
    Mono<Void> deleteImageMetadata(Long runnerId, Long imageId);
}