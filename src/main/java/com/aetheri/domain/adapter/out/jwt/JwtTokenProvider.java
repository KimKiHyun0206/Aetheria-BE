package com.aetheri.domain.adapter.out.jwt;

import com.aetheri.application.dto.jwt.RefreshTokenIssueResponse;
import com.aetheri.application.port.out.jwt.JwtTokenProviderPort;
import com.aetheri.infrastructure.config.properties.JWTProperties;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider implements JwtTokenProviderPort {

    private final SecretKey KEY;
    private final Duration ACCESS_TOKEN_VALIDITY_IN_HOUR;
    private final long REFRESH_TOKEN_VALIDATE_DAY;

    public JwtTokenProvider(JWTProperties jwtProperties, JwtKeyManager jwtKeyManager) {
        this.ACCESS_TOKEN_VALIDITY_IN_HOUR = jwtProperties.accessTokenValidityInHour();
        this.REFRESH_TOKEN_VALIDATE_DAY = jwtProperties.refreshTokenExpirationDays();
        KEY = jwtKeyManager.getKey();
    }

    // JWT 토큰 생성
    @Override
    public String generateAccessToken(Authentication authentication) {
        // 사용자 이름(Principal)을 토큰의 subject로 설정
        String subject = authentication.getName();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Instant now = Instant.now();
        Instant expiration = now.plus(ACCESS_TOKEN_VALIDITY_IN_HOUR);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .claim("roles", roles)
                .setExpiration(Date.from(expiration))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public RefreshTokenIssueResponse generateRefreshToken(Authentication authentication) {
        log.debug("[TokenProvider] createRefreshToken({})", authentication.getName());

        Instant issuedAt = Instant.now();
        String jti = UUID.randomUUID().toString();

        Claims claims = createRefreshTokenClaims(authentication.getName(), jti);

        Date issuedAtDate = Date.from(issuedAt);
        Date expirationDate = Date.from(issuedAt.plus(Duration.ofDays(REFRESH_TOKEN_VALIDATE_DAY)));

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedAtDate)
                .setExpiration(expirationDate)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();

        log.info("[TokenProvider] Refresh Token created for username: {}. Token length: {}", authentication.getName(), refreshToken.length());

        return RefreshTokenIssueResponse.of(refreshToken, jti, issuedAt);
    }

    private Claims createRefreshTokenClaims(String username, String jti) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(username));
        claims.put("jti", jti);
        return claims;
    }
}