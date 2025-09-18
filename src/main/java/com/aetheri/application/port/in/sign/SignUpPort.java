package com.aetheri.application.port.in.sign;

import reactor.core.publisher.Mono;

public interface SignUpPort {
    Mono<Void> signUp(Long id, String name);
}