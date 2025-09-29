package com.aetheri.application.port.in.imgfetch;

import reactor.core.publisher.Mono;

/**
 * 사용자가 입력하거나 요청한 이미지 경로의 유효성 검증과 관련된 비즈니스 로직을 정의하는 유즈케이스 인터페이스입니다.
 * 이 유즈케이스는 주어진 경로가 서버 파일 시스템이나 저장소에서 유효한지 확인하는 기능을 제공합니다.
 */
public interface ImagePathValidateUseCase {

    /**
     * 주어진 이미지 경로({@code path})의 유효성을 검증합니다.
     *
     * <p>이 메서드는 해당 경로가 실제 파일 시스템이나 저장소 규칙에 부합하는 유효한 형식인지,
     * 또는 접근 가능한 경로인지를 확인합니다.</p>
     *
     * @param path 유효성을 검증할 이미지 파일의 경로 문자열입니다.
     * @return 경로가 유효하다면 {@code true}를, 그렇지 않다면 {@code false}를 발행하는 {@code Mono<Boolean>} 객체입니다.
     */
    Mono<Boolean> isValidatePath(String path);
}