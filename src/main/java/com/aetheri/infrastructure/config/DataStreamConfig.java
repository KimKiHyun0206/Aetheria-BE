package com.aetheri.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

@Configuration
public class DataStreamConfig {
    @Bean
    public Jackson2JsonEncoder jackson2JsonEncoder(ObjectMapper objectMapper) {
        return new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON);
    }

    @Bean
    public DefaultDataBufferFactory defaultDataBufferFactory() {
        return new DefaultDataBufferFactory();
    }
}