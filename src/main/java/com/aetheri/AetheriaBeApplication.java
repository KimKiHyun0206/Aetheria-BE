package com.aetheri;

import com.aetheri.infrastructure.config.properties.JWTProperties;
import com.aetheri.infrastructure.config.properties.KakaoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({JWTProperties.class, KakaoProperties.class})
@SpringBootApplication
public class AetheriaBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AetheriaBeApplication.class, args);
    }

}
