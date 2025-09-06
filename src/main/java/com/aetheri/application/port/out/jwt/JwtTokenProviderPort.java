package com.aetheri.application.port.out.jwt;

import org.springframework.security.core.Authentication;

public interface JwtTokenProviderPort {
    String generateToken(Authentication authentication);
}