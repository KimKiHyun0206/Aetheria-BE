package com.aetheri.application.port.out.kakao;

import reactor.core.publisher.Mono;

/**
 * 카카오 API를 사용하여 로그아웃하기 위한 포트입니다.
 *
 * @see com.aetheri.domain.adapter.out.kakao.KakaoLogoutAdapter
 */
public interface KakaoLogoutPort {

    /**
     * 로그아웃하기 위한 메소드입니다.
     *
     * @param accessToken 카카오 액세스 토큰
     */
    Mono<Void> logout(String accessToken);
}