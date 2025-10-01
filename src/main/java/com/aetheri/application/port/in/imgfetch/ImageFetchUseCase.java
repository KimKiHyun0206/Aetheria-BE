package com.aetheri.application.port.in.imgfetch;

import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

/**
 * 서버 파일 시스템이나 저장소에 보관된 이미지를 조회하기 위한 유즈케이스 인터페이스입니다.
 * 이 유즈케이스는 이미지 파일 데이터를 {@code Resource} 형태로 제공하는 기능을 정의합니다.
 */
public interface ImageFetchUseCase {

    /**
     * 주어진 경로를 사용하여 서버에 저장된 이미지 리소스를 조회합니다.
     *
     * <p>이 메서드는 파일 경로({@code path})에 해당하는 이미지 데이터를 조회하고, 이를 Spring의 {@code Resource} 객체로 캡슐화하여 반환합니다.</p>
     *
     * @param path 이미지를 조회할 파일 시스템 또는 저장소 내의 경로입니다.
     * @return 조회된 이미지 파일 데이터를 담고 있는 {@code Resource} 객체를 발행하는 {@code Mono}입니다.
     */
    Mono<Resource> fetchImage(String path);
}