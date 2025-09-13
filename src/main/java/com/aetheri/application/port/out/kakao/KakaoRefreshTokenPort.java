package com.aetheri.application.port.out.kakao;

import com.aetheri.application.dto.KakaoTokenResponse;
import reactor.core.publisher.Mono;

/**
 * 카카오에게 리프래쉬 토큰을 사용하여 토큰 재발급을 요청하는 포트입니다.
 *
 * @see com.aetheri.domain.adapter.out.kakao.KakaoRefreshTokenAdapter
 */
public interface KakaoRefreshTokenPort {

    /**
     * 카카오 API에 리프레쉬 토큰을 사용하여 액세스 토큰과 리프레쉬 토큰 재발급을 요청합니다.
     *
     * @param refreshToken 카카오 리프래쉬 토큰
     */
    Mono<KakaoTokenResponse> refreshAccessToken(String refreshToken);
}