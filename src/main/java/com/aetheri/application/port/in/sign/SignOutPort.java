package com.aetheri.application.port.in.sign;

import reactor.core.publisher.Mono;

public interface SignOutPort {
    Mono<Void> signOut(Long runnerId);
}