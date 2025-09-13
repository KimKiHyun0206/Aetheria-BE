package com.aetheri.application.port.out.kakao;

import reactor.core.publisher.Mono;

/**
 * 카카오 API를 사용하여 회원 탈퇴를 하기 위한 포트입니다.
 *
 * @see com.aetheri.domain.adapter.out.kakao.KakaoUnlinkAdapter
 */
public interface KakaoUnlinkPort {

    /**
     * 카카오 API에게 요청을 보내는 메소드입니다.
     *
     * @param accessToken 카카오 액세스 토큰
     */
    Mono<Long> unlink(String accessToken);
}