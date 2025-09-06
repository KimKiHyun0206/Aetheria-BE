package com.aetheri.domain.adapter.out.jwt;

import com.aetheri.application.port.out.jwt.JwtTokenValidatorPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtTokenValidator implements JwtTokenValidatorPort {

    @Value("${jwt.secret-key}")
    private String secretKey;

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


    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 유효성 검증 실패 시 예외 처리
            return false;
        }
    }
}