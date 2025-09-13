package com.aetheri.infrastructure.config;

import com.aetheri.infrastructure.config.properties.KakaoProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient kakaoWebClient(WebClient.Builder webClientBuilder, KakaoProperties kakaoProperties) {
        return webClientBuilder.baseUrl(kakaoProperties.api())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                                .responseTimeout(Duration.ofSeconds(5))
                                .doOnConnected(c -> c
                                        .addHandlerLast(new ReadTimeoutHandler(5))
                                        .addHandlerLast(new WriteTimeoutHandler(5)))
                ))
                .build();
    }

    @Bean
    public WebClient kakaoAuthWebClient(WebClient.Builder webClientBuilder, KakaoProperties kakaoProperties) {
        return webClientBuilder.baseUrl(kakaoProperties.authApi())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                                .responseTimeout(Duration.ofSeconds(5))
                                .doOnConnected(c -> c
                                        .addHandlerLast(new ReadTimeoutHandler(5))
                                        .addHandlerLast(new WriteTimeoutHandler(5)))
                ))
                .build();
    }
}