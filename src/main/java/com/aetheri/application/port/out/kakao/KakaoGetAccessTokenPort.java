package com.aetheri.application.port.out.kakao;

import com.aetheri.interfaces.dto.kakao.KakaoTokenResponseDto;
import reactor.core.publisher.Mono;

public interface KakaoGetAccessTokenPort {
    Mono<KakaoTokenResponseDto> tokenRequest(String code);
}