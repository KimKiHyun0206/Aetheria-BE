package com.aetheri.application.port.out.kakao;

import com.aetheri.interfaces.dto.kakao.KakaoUserInfoResponseDto;
import reactor.core.publisher.Mono;

public interface KakaoUserInformationInquiryPort {
    Mono<KakaoUserInfoResponseDto> userInformationInquiry(String accessToken);
}