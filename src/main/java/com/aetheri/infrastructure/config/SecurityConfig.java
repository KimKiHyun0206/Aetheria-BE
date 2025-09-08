package com.aetheri.infrastructure.config;

import com.aetheri.application.port.out.jwt.JwtTokenProviderPort;
import com.aetheri.application.port.out.jwt.JwtTokenResolverPort;
import com.aetheri.application.port.out.jwt.JwtTokenValidatorPort;
import com.aetheri.application.service.redis.refreshtoken.RefreshTokenService;
import com.aetheri.domain.adapter.in.jwt.JwtAuthenticationFilter;
import com.aetheri.infrastructure.config.properties.JWTProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;


@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenResolverPort jwtTokenResolverPort;
    private final JwtTokenValidatorPort jwtTokenValidatorPort;
    private final JWTProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // JWT를 사용하기 때문에 CSRF를 비활성화한다.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                /*
                Spring Security의 요청 캐싱 기능을 비활성화합니다.
                이는 인증되지 않은 사용자가 보호된 리소스에 접근했을 때,
                로그인 성공 후 원래 요청했던 페이지로 자동으로 리다이렉션하는 기능을 끕니다.
                API 서버와 같이 리다이렉션이 필요 없는 경우에 사용됩니다.
                */
                .requestCache(ServerHttpSecurity.RequestCacheSpec::disable)

                // 인증에 따른 접근 가능 여부 설정
                .authorizeExchange(exchanges -> exchanges
                        // Spring Actuator 접근 URL 허용
                        .pathMatchers("/actuator/**").permitAll()
                        // Swagger 접근에 필요한 URL
                        .pathMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/swagger-ui/**",
                                "/api-docs/**"
                        ).permitAll()
                        .pathMatchers("/api/hello/**").permitAll()
                        .pathMatchers("/api/oauth2/**", "/login/**").permitAll()
                        // 위에 명시된 경로를 제외한 모든 요청은 인증된 사용자만 접근할 수 있다.
                        .anyExchange().authenticated()
                )
                // formLogin 기능도 명시적으로 비활성화
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                // JWT를 사용한 세션리스(stateless) 인증이므로, 세션 저장소를 사용하지 않도록 설정한다.
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                // SecurityWebFiltersOrder.AUTHENTICATION 위치에 JwtAuthenticationFilter를 추가한다.
                // 이 필터는 HTTP 요청 헤더에서 JWT 토큰을 추출하고, 토큰의 유효성을 검사하여 인증 객체(Authentication)를 생성한다.
                .addFilterAt(
                        new JwtAuthenticationFilter(
                                jwtTokenValidatorPort,
                                jwtTokenResolverPort,
                                refreshTokenService,
                                jwtProperties
                        ),
                        SecurityWebFiltersOrder.AUTHENTICATION
                )
                .build();
    }
}