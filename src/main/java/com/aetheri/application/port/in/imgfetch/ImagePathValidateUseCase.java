package com.aetheri.application.port.in.imgfetch;

import reactor.core.publisher.Mono;

/**
 * 사용자가 보낸 이미지 경로가 유효한지 검증하기 위한 유즈케이스
 * */
public interface ImagePathValidateUseCase {

    Mono<Boolean> isValidatePath(String path);
}