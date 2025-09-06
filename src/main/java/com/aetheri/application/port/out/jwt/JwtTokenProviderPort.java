package com.aetheri.application.port.out.jwt;

import com.aetheri.application.dto.jwt.RefreshTokenIssueResponse;
import org.springframework.security.core.Authentication;

public interface JwtTokenProviderPort {
    String generateAccessToken(Authentication authentication);
    RefreshTokenIssueResponse generateRefreshToken(Authentication authentication);
}