package com.aetheri.application.service.image;

import com.aetheri.application.port.in.image.DeleteImageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeleteImageService implements DeleteImageUseCase {
    @Override
    public Mono<Void> delete(Long runnerId, Long imageId) {
        return null;
    }
}