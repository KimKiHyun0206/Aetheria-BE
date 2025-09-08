package com.aetheri.domain.adapter.out.jwt;

import com.aetheri.application.port.out.jwt.JwtTokenValidatorPort;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtTokenValidator implements JwtTokenValidatorPort {
    private final SecretKey KEY;

    public JwtTokenValidator(JwtKeyManager jwtKeyManager) {
        this.KEY = jwtKeyManager.getKey();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 유효성 검증 실패 시 예외 처리
            return false;
        }
    }
}