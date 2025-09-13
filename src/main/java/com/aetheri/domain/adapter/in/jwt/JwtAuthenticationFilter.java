package com.aetheri.domain.adapter.in.jwt;

import com.aetheri.application.port.out.jwt.JwtTokenResolverPort;
import com.aetheri.application.port.out.jwt.JwtTokenValidatorPort;
import com.aetheri.application.service.redis.refreshtoken.RefreshTokenService;
import com.aetheri.infrastructure.config.properties.JWTProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtTokenValidatorPort jwtTokenValidatorPort;
    private final JwtTokenResolverPort jwtTokenResolverPort;
    private final RefreshTokenService refreshTokenService; // 추가
    private final JWTProperties jwtProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String accessToken = resolveToken(exchange.getRequest());

        if (accessToken != null) {
            if (jwtTokenValidatorPort.validateToken(accessToken)) {
                // 정상 액세스 토큰
                return authenticateAndContinue(exchange, chain, accessToken);
            } else {
                // 만료된 액세스 토큰 → 리프레시 토큰 확인
                String refreshToken = getRefreshTokenFromCookie(exchange);
                if (refreshToken != null && !refreshToken.isBlank()) {
                    return refreshTokenService.reissueTokens(refreshToken)
                            .flatMap(tokenResponse -> {
                                // 새 액세스 토큰은 헤더
                                exchange.getResponse().getHeaders().set(jwtProperties.accessTokenHeader(), "Bearer " + tokenResponse.accessToken());
                                exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, jwtProperties.accessTokenHeader());

                                // 새 리프레시 토큰은 HttpOnly 쿠키로 세팅
                                ResponseCookie cookie = ResponseCookie.from(
                                                jwtProperties.refreshTokenCookie(),
                                                tokenResponse.refreshTokenIssueResponse().refreshToken()
                                        )
                                        .httpOnly(true)
                                        .secure(true) // HTTPS에서만
                                        .maxAge(Duration.ofDays(jwtProperties.refreshTokenExpirationDays()))
                                        .sameSite("Strict")
                                        .build();
                                exchange.getResponse().addCookie(cookie);

                                // SecurityContext 인증 세팅
                                Long id = jwtTokenResolverPort.getIdFromToken(tokenResponse.accessToken());
                                List<GrantedAuthority> authorities = jwtTokenResolverPort.getRolesFromToken(tokenResponse.accessToken())
                                        .stream()
                                        .filter(role -> role != null && !role.isBlank()) // null, 빈 문자열 제거
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList());

                                Authentication authentication = generateAuthentication(id, authorities);

                                return chain.filter(exchange)
                                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                            });
                }
            }
        }

        return chain.filter(exchange); // 토큰 없거나 유효하지 않은 경우
    }

    private String getRefreshTokenFromCookie(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        HttpCookie cookie = request.getCookies().getFirst(jwtProperties.refreshTokenCookie());

        if (cookie != null) {
            return cookie.getValue();
        }
        return null; // 쿠키가 없는 경우 null 반환
    }

    private Mono<Void> authenticateAndContinue(ServerWebExchange exchange, WebFilterChain chain, String token) {
        Long id = jwtTokenResolverPort.getIdFromToken(token);
        List<GrantedAuthority> authorities = jwtTokenResolverPort.getRolesFromToken(token)
                .stream()
                .filter(role -> role != null && !role.isBlank()) // null, 빈 문자열 제거
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Authentication authentication = generateAuthentication(id, authorities);

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(jwtProperties.accessTokenHeader());
        if (StringUtils.hasText(bearerToken) && StringUtils.startsWithIgnoreCase(bearerToken, "Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    private Authentication generateAuthentication(Long id, List<GrantedAuthority> authorities) {
        UserDetails userDetails = new User(String.valueOf(id), "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }
}
