package com.aetheri.domain.adapter.out.jwt;

import com.aetheri.application.port.out.jwt.JwtTokenResolverPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
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
        String sub = claims(token).getSubject();
        try {
            return Long.valueOf(sub);
        } catch (NumberFormatException e) {
            throw new BusinessException(
                    ErrorMessage.JWT_SUBJECT_IS_NOT_NUMBER,
                    "JWT 토큰 값이 유효하지 않습니다."
            );
        }
    }

    // 권한 가져오기
    @Override
    public List<String> getRolesFromToken(String token) {
        List<?> roles = claims(token).get("roles", List.class);

        return roles == null ?
                List.of() :
                roles.stream().map(String::valueOf).toList();
    }

    private Claims claims(String token) {
        if (token == null || token.isBlank()) {
            throw new BusinessException(
                    ErrorMessage.JWT_TOKEN_IS_EMPTY,
                    "JWT 토큰이 비어있습니다."
            );
        }
        return parser.parseClaimsJws(token).getBody();
    }
}