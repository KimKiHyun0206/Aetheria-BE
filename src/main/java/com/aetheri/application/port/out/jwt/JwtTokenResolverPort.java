package com.aetheri.application.port.out.jwt;

import java.util.List;

/**
 * JWT 토큰 내부의 정보들을 얻기 위한 포트입니다.
 *
 * @see com.aetheri.domain.adapter.out.jwt.JwtTokenResolver
 */
public interface JwtTokenResolverPort {

    /**
     * 토큰에서 ID를 가져오는 메소드입니다.
     */
    Long getIdFromToken(String token);

    /**
     * 토큰에서 사용자 권한을 가져오는 메소드입니다.
     */
    List<String> getRolesFromToken(String token);
}