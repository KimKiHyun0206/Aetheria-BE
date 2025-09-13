package com.aetheri.infrastructure.config;

import com.aetheri.infrastructure.config.properties.KakaoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient kakaoWebClient(WebClient.Builder webClientBuilder, KakaoProperties kakaoProperties) {
        return webClientBuilder.baseUrl(kakaoProperties.api()).build();
    }

    @Bean
    public WebClient kakaoAuthWebClient(WebClient.Builder webClientBuilder, KakaoProperties kakaoProperties) {
        return webClientBuilder.baseUrl(kakaoProperties.authApi()).build();
    }
}