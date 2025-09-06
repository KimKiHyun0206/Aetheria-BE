package com.aetheri.domain.adapter.out.jwt;

import com.aetheri.application.port.out.jwt.JwtTokenProviderPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider implements JwtTokenProviderPort {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    private SecretKey secret;

    @PostConstruct
    public void init() {
        // 시크릿 키를 Base64로 인코딩한 후 HMAC SHA 키로 변환
        final byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretKey);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    ErrorMessage.INTERNAL_SERVER_ERROR,
                    "jwt.secret-key는 Base64 인코딩된 문자열이어야 합니다."
            );
        }
        if (keyBytes.length < 32) { // 256bit
            throw new BusinessException(
                    ErrorMessage.INTERNAL_SERVER_ERROR,
                    "jwt.secret-key는 HS256에 적합한 최소 256비트 이상이어야 합니다."
            );
        }
        this.secret = Keys.hmacShaKeyFor(keyBytes);
    }

    // JWT 토큰 생성
    @Override
    public String generateToken(Authentication authentication) {
        // 사용자 이름(Principal)을 토큰의 subject로 설정
        String subject = authentication.getName();
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(subject)        // 토큰 주체
                .setIssuedAt(now)           // 발행 시간
                .claim("roles", roles)   // roles 클레임에 권한 정보 추가
                .setExpiration(expiration)  // 만료 시간
                .signWith(secret, SignatureAlgorithm.HS256) // 서명 (시크릿 키)
                .compact();
    }
}