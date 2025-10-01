package com.aetheri.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

/**
 * 데이터 스트림 처리와 관련된 주요 컴포넌트들을 설정하는 Spring {@code @Configuration} 클래스입니다.
 *
 * <p>주로 {@code WebClient}와 같은 반응형(Reactive) 환경에서 JSON 데이터 인코딩 및
 * 데이터 버퍼 생성에 필요한 빈({@code Bean})들을 정의합니다.</p>
 */
@Configuration
public class DataStreamConfig {

    /**
     * JSON 데이터를 인코딩하기 위한 {@link Jackson2JsonEncoder} 빈을 생성합니다.
     *
     * <p>이 인코더는 주로 {@code WebClient}가 HTTP 요청 본문을 JSON 형식으로 변환할 때 사용됩니다.</p>
     *
     * @param objectMapper Spring에서 자동 설정된 {@link ObjectMapper} 인스턴스입니다.
     * @return {@code application/json} 미디어 타입을 처리하는 {@code Jackson2JsonEncoder}입니다.
     */
    @Bean
    public Jackson2JsonEncoder jackson2JsonEncoder(ObjectMapper objectMapper) {
        return new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON);
    }

    /**
     * 데이터 버퍼를 생성하기 위한 {@link DefaultDataBufferFactory} 빈을 생성합니다.
     *
     * <p>반응형 스트림 처리 과정에서 데이터를 메모리에 담는 {@code DataBuffer} 객체를
     * 생성할 때 사용되는 기본 팩토리입니다.</p>
     *
     * @return 기본 {@code DefaultDataBufferFactory} 인스턴스입니다.
     */
    @Bean
    public DefaultDataBufferFactory defaultDataBufferFactory() {
        return new DefaultDataBufferFactory();
    }
}