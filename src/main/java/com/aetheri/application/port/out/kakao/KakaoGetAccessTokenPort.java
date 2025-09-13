package com.aetheri.application.port.out.kakao;

import com.aetheri.application.dto.KakaoTokenResponse;
import reactor.core.publisher.Mono;

/**
 * 카카오에서 액세스 토큰을 가져오기 위한 포트입니다.
 *
 * @see com.aetheri.domain.adapter.out.kakao.KakaoGetAccessTokenAdapter
 */
public interface KakaoGetAccessTokenPort {

    /**
     * 카카오 API에게 액세스 토큰 발급을 요청하는 메소드입니다.
     *
     * @param code 카카오가 로그인을 성공했다고 보내준 인증 코드
     */
    Mono<KakaoTokenResponse> tokenRequest(String code);
}