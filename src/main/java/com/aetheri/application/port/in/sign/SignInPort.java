package com.aetheri.application.port.in.sign;

import com.aetheri.application.dto.SignInResponse;
import reactor.core.publisher.Mono;

public interface SignInPort {
    Mono<SignInResponse> login(String code);
}