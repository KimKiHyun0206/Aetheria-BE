package com.aetheri.domain.adapter.out.jwt;

import com.aetheri.application.port.out.jwt.JwtTokenResolverPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.List;

@Component
public class JwtTokenResolver implements JwtTokenResolverPort {
    @Value("${jwt.secret-key}")
    private String secretKey;

    private JwtParser parser;

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
        SecretKey secret = Keys.hmacShaKeyFor(keyBytes);
        this.parser = Jwts.parserBuilder()
                .setSigningKey(secret)
                .setAllowedClockSkewSeconds(30) // 시계 오차 허용
                .build();
    }


    // 토큰에서 사용자 이름 추출
    @Override
    public String getUsernameFromToken(String token) {
        return parser
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 권한 가져오기
    @Override
    public List<String> getRolesFromToken(String token) {
        Claims claims = parser.parseClaimsJws(token).getBody();

        List<?> roles = claims.get("roles", List.class);
        return roles == null ? List.of() : roles.stream().map(String::valueOf).toList();
    }
}