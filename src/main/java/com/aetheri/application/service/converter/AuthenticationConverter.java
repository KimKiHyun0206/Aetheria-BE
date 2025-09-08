package com.aetheri.application.service.converter;

import com.aetheri.infrastructure.persistence.Runner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationConverter {
    public static Authentication toAuthentication(Runner runner) {
        return new UsernamePasswordAuthenticationToken(
                runner.getId(),       // principal
                null,                 // credentials
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    public static Authentication toAuthentication(Long runnerId) {
        return new UsernamePasswordAuthenticationToken(
                runnerId,               // principal
                null,                   // credentials
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}