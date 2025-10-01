package com.aetheri.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * **카카오 API 통신 및 인증 관련 설정 값**을 외부 설정 파일(예: application.yml)로부터
 * 주입받기 위한 레코드 클래스입니다.
 *
 * <p>설정 파일에서 {@code kakao} 프리픽스({@code prefix = "kakao"})로 시작하는 속성들을
 * 이 레코드의 필드에 자동으로 바인딩합니다.</p>
 */
@ConfigurationProperties(prefix = "kakao")
public record KakaoProperties(

        /**
         * 카카오 인증 요청 시 사용되는 **클라이언트 ID (애플리케이션 REST API 키)**입니다.
         */
        String clientId,

        /**
         * 카카오 로그인 완료 후 인증 코드를 전달받을 **리다이렉트 URI**입니다.
         */
        String redirectUri,

        /**
         * 카카오 **인증(토큰 발급/갱신 등) API**의 기본 URL 또는 프리픽스입니다. (예: {@code https://kauth.kakao.com})
         */
        String authApi,

        /**
         * 카카오 **일반 API (사용자 정보 조회, 로그아웃 등)**의 기본 URL 또는 프리픽스입니다. (예: {@code https://kapi.kakao.com})
         */
        String api,

        /**
         * 카카오 API 호출 시 응답을 기다리는 **허용 시간(Timeout)**을 나타내는 초(Seconds) 단위 값입니다.
         */
        int responseTimeoutSeconds,

        /**
         * 카카오 API와의 초기 **연결(Connection)을 유지하거나 설정하는 데 허용되는 시간**을 나타내는 밀리초(Millis) 단위 값입니다.
         */
        int connectTimeoutMillis,

        /**
         * 카카오 API 통신 시 **일반적인 타임아웃** 설정값입니다. (일반적으로 {@code responseTimeoutSeconds}와 유사)
         * 단위는 초(Seconds)입니다.
         */
        int timeOutSeconds
) {
}