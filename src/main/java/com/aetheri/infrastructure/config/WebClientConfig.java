package com.aetheri.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient kakaoWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl("https://kapi.kakao.com").build();
    }

    @Bean
    public WebClient kakaoAuthWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl("https://kauth.kakao.com").build();
    }
}