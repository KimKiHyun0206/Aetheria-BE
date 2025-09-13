package com.aetheri.interfaces.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserInfoResponseDto(

        // 회원 번호
        @JsonProperty("id")
        Long id,

        // 자동 연결 설정을 비활성화한 경우만 존재.
        // true : 연결 상태, false : 연결 대기 상태
        @JsonProperty("has_signed_up")
        Boolean hasSignedUp,

        // 서비스에 연결 완료된 시각. UTC
        @JsonProperty("connected_at")
        Date connectedAt,

        // 카카오싱크 간편가입을 통해 로그인한 시각. UTC
        @JsonProperty("synched_at")
        Date synchedAt,

        // 사용자 프로퍼티
        @JsonProperty("properties")
        HashMap<String, String> properties,

        // 카카오 계정 정보
        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount,

        // uuid 등 추가 정보
        @JsonProperty("for_partner")
        Partner partner
) {
}