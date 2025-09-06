package com.aetheri.application.port.out.kakao;

import reactor.core.publisher.Mono;

public interface KakaoUnlinkPort {
    Mono<Long> unlink(String accessToken);
}