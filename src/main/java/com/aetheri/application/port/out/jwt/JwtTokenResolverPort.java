package com.aetheri.application.port.out.jwt;

import java.util.List;

public interface JwtTokenResolverPort {
    String getUsernameFromToken(String token);
    List<String> getRolesFromToken(String token);

}