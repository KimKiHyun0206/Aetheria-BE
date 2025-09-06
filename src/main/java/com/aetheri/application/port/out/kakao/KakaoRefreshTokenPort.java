package com.aetheri.application.port.out.kakao;

import com.aetheri.application.dto.KakaoTokenResponse;
import reactor.core.publisher.Mono;

public interface KakaoRefreshTokenPort {
    Mono<KakaoTokenResponse> refreshAccessToken(String refreshToken);
}