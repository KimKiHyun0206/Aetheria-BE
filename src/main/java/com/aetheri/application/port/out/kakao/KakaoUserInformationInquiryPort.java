package com.aetheri.application.port.out.kakao;

import com.aetheri.interfaces.dto.kakao.KakaoUserInfoResponseDto;
import reactor.core.publisher.Mono;

/**
 * 카카오 API를 사용해 사용자 정보를 조회하기 위한 포트입니다.
 *
 * @see com.aetheri.domain.adapter.out.kakao.KakaoUserInformationInquiryAdapter
 */
public interface KakaoUserInformationInquiryPort {

    /**
     * 카카오 회원 정보 조회 API를 사용하기 위한 메소드입니다.
     *
     * @param accessToken 카카오 액세스 토큰
     */
    Mono<KakaoUserInfoResponseDto> userInformationInquiry(String accessToken);
}