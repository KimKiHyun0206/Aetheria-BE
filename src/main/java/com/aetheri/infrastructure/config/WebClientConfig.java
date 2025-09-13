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

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final KakaoProperties kakaoProperties;

    @Bean
    public WebClient kakaoWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(kakaoProperties.api())
                .clientConnector(commonConnector(
                        kakaoProperties.connectTimeoutMillis(),
                        kakaoProperties.responseTimeoutSeconds())
                )
                .build();
    }

    @Bean
    public WebClient kakaoAuthWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(kakaoProperties.authApi())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                                .responseTimeout(Duration.ofSeconds(kakaoProperties.timeOutSeconds()))
                                .doOnConnected(c -> c
                                        .addHandlerLast(new ReadTimeoutHandler(kakaoProperties.timeOutSeconds()))
                                        .addHandlerLast(new WriteTimeoutHandler(kakaoProperties.timeOutSeconds())))
                ))
                .build();
    }

    private ReactorClientHttpConnector commonConnector(int connectTimeoutMillis, int responseTimeoutSeconds) {
        return new ReactorClientHttpConnector(
                HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
                        .responseTimeout(Duration.ofSeconds(responseTimeoutSeconds))
                        .doOnConnected(c -> c
                                .addHandlerLast(new ReadTimeoutHandler(responseTimeoutSeconds))
                                .addHandlerLast(new WriteTimeoutHandler(responseTimeoutSeconds)))
        );
    }
}