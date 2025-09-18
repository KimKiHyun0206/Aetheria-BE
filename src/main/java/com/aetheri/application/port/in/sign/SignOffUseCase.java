package com.aetheri.application.port.in.sign;

import reactor.core.publisher.Mono;

public interface SignOffUseCase {
    Mono<Void> signOff(Long runnerId);
}