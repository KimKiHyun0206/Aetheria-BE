package com.aetheri.application.port.out.kakao;

import reactor.core.publisher.Mono;

public interface KakaoLogoutPort {
    Mono<Void> logout(String accessToken);
}