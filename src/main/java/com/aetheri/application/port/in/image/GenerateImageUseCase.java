package com.aetheri.application.port.in.image;

import com.aetheri.application.dto.image.GenerateImageRequest;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

/**
 * 이미지 생성 요청 포트
 * */
public interface GenerateImageUseCase {
    Mono<Resource> generateImage(GenerateImageRequest request);
}