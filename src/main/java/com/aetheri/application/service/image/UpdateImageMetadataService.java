package com.aetheri.application.service.image;

import com.aetheri.application.dto.image.UpdateImageMetadataRequest;
import com.aetheri.application.port.in.image.UpdateImageMetadataUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpdateImageMetadataService implements UpdateImageMetadataUseCase {
    @Override
    public Mono<Long> update(UpdateImageMetadataRequest request) {
        return null;
    }
}