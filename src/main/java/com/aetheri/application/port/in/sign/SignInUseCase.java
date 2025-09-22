package com.aetheri.application.port.in.sign;

import com.aetheri.application.dto.SignInResponse;
import reactor.core.publisher.Mono;

public interface SignInUseCase {
    Mono<SignInResponse> signIn(String code);
}