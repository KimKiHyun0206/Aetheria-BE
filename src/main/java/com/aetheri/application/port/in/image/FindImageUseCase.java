package com.aetheri.application.port.in.image;

import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 이미지 조회 유즈케이스
 * */
public interface FindImageUseCase {
    Mono<Resource> findImageById(Long imageId);
    Flux<Resource> findImageByRunnerId(Long runnerId);
}