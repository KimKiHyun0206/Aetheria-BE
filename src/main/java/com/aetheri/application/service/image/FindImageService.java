package com.aetheri.application.service.image;

import com.aetheri.application.port.in.image.FindImageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FindImageService implements FindImageUseCase {
    @Override
    public Mono<Resource> findImageById(Long imageId) {
        return null;
    }

    @Override
    public Flux<Resource> findImageByRunnerId(Long runnerId) {
        return null;
    }
}