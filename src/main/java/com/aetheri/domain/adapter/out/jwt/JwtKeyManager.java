package com.aetheri.domain.adapter.out.jwt;

import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.config.properties.JWTProperties;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * JWT 키를 생성해주는 컴포넌트
 * */
@Getter
@Component
public class JwtKeyManager {
    private final SecretKey key;
    private final JwtParser parser;

    public JwtKeyManager(JWTProperties jwtProperties) {
        final byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    ErrorMessage.INTERNAL_SERVER_ERROR,
                    "jwt.secret은 Base64 인코딩된 문자열이어야 합니다."
            );
        }
        if (keyBytes.length < 32) { // 256bit
            throw new BusinessException(
                    ErrorMessage.INTERNAL_SERVER_ERROR,
                    "jwt.secret은 HS256에 적합한 최소 256비트(32바이트) 이상이어야 합니다."
            );
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(jwtProperties.allowedClockSkewSeconds())
                .build();
    }
}