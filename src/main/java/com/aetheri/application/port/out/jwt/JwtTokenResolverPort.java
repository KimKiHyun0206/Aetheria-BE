package com.aetheri.application.port.out.jwt;

import java.util.List;

public interface JwtTokenResolverPort {
    Long getIdFromToken(String token);
    List<String> getRolesFromToken(String token);
}