package com.aetheri.application.port.in.sign;

import reactor.core.publisher.Mono;

public interface SignOutUseCase {
    Mono<Void> signOut(Long runnerId);
}