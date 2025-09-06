package com.aetheri.application.port.out.jwt;

public interface JwtTokenValidatorPort {
    boolean validateToken(String token);
}
