package com.aetheri.infrastructure.config;

import com.aetheri.infrastructure.config.properties.KakaoProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * 카카오 API와의 비동기 통신을 위해 **{@code WebClient} 인스턴스 및 커넥션 설정을 정의**하는
 * Spring {@code @Configuration} 클래스입니다.
 *
 * <p>카카오의 일반 API와 인증(Auth) API를 위해 각각 다른 {@code WebClient} 빈을 생성하며,
 * 각 빈에 타임아웃 설정을 적용합니다.</p>
 */
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final KakaoProperties kakaoProperties;

    /**
     * 카카오의 **일반 API (사용자 정보, 로그아웃, 연결 해제 등)**와 통신하기 위한 {@code WebClient} 빈을 생성합니다.
     *
     * <p>{@code kakaoProperties}에 설정된 {@code api} 기본 URL과 타임아웃 설정을 사용합니다.</p>
     *
     * @param webClientBuilder Spring에서 자동 주입된 {@code WebClient.Builder}입니다.
     * @return 기본 API URL이 설정된 {@code WebClient} 인스턴스입니다.
     */
    @Bean
    public WebClient kakaoWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(kakaoProperties.api())
                // 일반 API용 공통 커넥터 설정을 적용합니다.
                .clientConnector(commonConnector(
                        kakaoProperties.connectTimeoutMillis(),
                        kakaoProperties.responseTimeoutSeconds())
                )
                .build();
    }

    /**
     * 카카오의 **인증 API (토큰 발급, 갱신 등)**와 통신하기 위한 {@code WebClient} 빈을 생성합니다.
     *
     * <p>{@code kakaoProperties}에 설정된 {@code authApi} 기본 URL과 전용 타임아웃 설정을 사용합니다.</p>
     *
     * @param webClientBuilder Spring에서 자동 주입된 {@code WebClient.Builder}입니다.
     * @return 인증 API URL이 설정된 {@code WebClient} 인스턴스입니다.
     */
    @Bean
    public WebClient kakaoAuthWebClient(WebClient.Builder webClientBuilder) {
        // 인증 API는 별도의 타임아웃 설정을 가지므로 인라인으로 커넥터를 생성합니다.
        return webClientBuilder.baseUrl(kakaoProperties.authApi())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                // 연결 타임아웃 설정 (하드코딩된 5초)
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                                // 응답 전체 타임아웃 설정
                                .responseTimeout(Duration.ofSeconds(kakaoProperties.timeOutSeconds()))
                                // 연결이 수립된 후, Netty 핸들러를 추가하여 Read/Write 타임아웃 설정
                                .doOnConnected(c -> c
                                        .addHandlerLast(new ReadTimeoutHandler(kakaoProperties.timeOutSeconds()))
                                        .addHandlerLast(new WriteTimeoutHandler(kakaoProperties.timeOutSeconds())))
                ))
                .build();
    }

    /**
     * **공통적으로 사용되는 {@code WebClient} 커넥터 설정**을 생성합니다.
     *
     * <p>{@code HttpClient}를 사용하여 연결 타임아웃, 응답 타임아웃, Netty의 Read/Write 타임아웃을 설정합니다.</p>
     *
     * @param connectTimeoutMillis 연결 수립에 허용되는 시간(밀리초)입니다.
     * @param responseTimeoutSeconds 응답 수신에 허용되는 총 시간(초)입니다.
     * @return 설정된 타임아웃 정책이 적용된 {@code ReactorClientHttpConnector}입니다.
     */
    private ReactorClientHttpConnector commonConnector(int connectTimeoutMillis, int responseTimeoutSeconds) {
        return new ReactorClientHttpConnector(
                HttpClient.create()
                        // Netty ChannelOption을 사용하여 연결 타임아웃 설정
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
                        // Reactor Netty의 응답 전체 타임아웃 설정
                        .responseTimeout(Duration.ofSeconds(responseTimeoutSeconds))
                        // 연결 수립 후, Netty 파이프라인에 Read/Write 타임아웃 핸들러 추가
                        .doOnConnected(c -> c
                                .addHandlerLast(new ReadTimeoutHandler(responseTimeoutSeconds))
                                .addHandlerLast(new WriteTimeoutHandler(responseTimeoutSeconds)))
        );
    }
}