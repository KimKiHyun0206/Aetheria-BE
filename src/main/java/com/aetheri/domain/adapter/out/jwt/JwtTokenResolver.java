package com.aetheri.domain.adapter.out.jwt;

import com.aetheri.application.port.out.jwt.JwtTokenResolverPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtTokenResolver implements JwtTokenResolverPort {
    private final JwtParser parser;

    public JwtTokenResolver(JwtKeyManager jwtKeyManager) {
        this.parser = jwtKeyManager.getParser();
    }

    // 토큰에서 사용자 이름 추출
    @Override
    public Long getIdFromToken(String token) {
        return Long.valueOf(parser
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    // 권한 가져오기
    @Override
    public List<String> getRolesFromToken(String token) {
        Claims claims = parser.parseClaimsJws(token).getBody();

        List<?> roles = claims.get("roles", List.class);
        return roles == null ? List.of() : roles.stream().map(String::valueOf).toList();
    }
}