package com.aetheri.application.port.in.imgfetch;

import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

/**
 * 서버에 저장된 이미지를 조회하기 위한 유즈케이스
 * */
public interface ImageFetchUseCase {

    /**
     * 이미지를 조회하기 위한 포트로 매개변수의 경로를 사용하여 조회한다.
     *
     * @param path 이미지를 조회할 경로
     * */
    Mono<Resource> fetchImage(String path);
}