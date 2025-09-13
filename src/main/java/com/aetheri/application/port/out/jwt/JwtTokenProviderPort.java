package com.aetheri.application.port.out.jwt;

import com.aetheri.application.dto.jwt.RefreshTokenIssueResponse;
import org.springframework.security.core.Authentication;

/**
 * JWT 토큰을 생성하기 위한 포트입니다.
 *
 * @see com.aetheri.domain.adapter.out.jwt.JwtTokenProvider
 */
public interface JwtTokenProviderPort {
    /**
     * 액세스 토큰을 생성합니다.
     */
    String generateAccessToken(Authentication authentication);

    /**
     * 리프레쉬 토큰을 생성하여 토큰 정보와 같이 리턴합니다.
     *
     * @see RefreshTokenIssueResponse
     */
    RefreshTokenIssueResponse generateRefreshToken(Authentication authentication);
}