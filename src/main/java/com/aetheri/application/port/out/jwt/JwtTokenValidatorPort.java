package com.aetheri.application.port.out.jwt;

/**
 * JWT 토큰이 유효한지 검사하기 위한 포트입니다.
 *
 * @see com.aetheri.domain.adapter.out.jwt.JwtTokenValidator
 */
public interface JwtTokenValidatorPort {

    /**
     * JWT 토큰이 유요한지 검사하는 메소드입니다.
     */
    boolean validateToken(String token);
}