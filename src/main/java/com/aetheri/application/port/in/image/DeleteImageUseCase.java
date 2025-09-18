package com.aetheri.application.port.in.image;

import reactor.core.publisher.Mono;

/**
 * 이미지 삭제 유즈케이스
 * */
public interface DeleteImageUseCase {
    Mono<Void> delete(Long runnerId, Long imageId);
}