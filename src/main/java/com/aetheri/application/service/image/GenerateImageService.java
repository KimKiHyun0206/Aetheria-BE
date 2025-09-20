package com.aetheri.application.service.image;

import com.aetheri.application.dto.image.GenerateImageRequest;
import com.aetheri.application.port.in.image.GenerateImageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GenerateImageService implements GenerateImageUseCase {
    @Override
    public Mono<Resource> generateImage(GenerateImageRequest request) {
        return null;
    }
}