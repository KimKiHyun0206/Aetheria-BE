package com.aetheri.application.service.converter;

import com.aetheri.infrastructure.persistence.Runner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 사용자 엔티티 또는 사용자의 ID를 가지고 Authentication 객체를 만들기 위한 컨버터
 */
@Service
public class AuthenticationConverter {

    /**
     * 사용자 엔티티를 가지고 Authentication 객체를 만듭니다.
     *
     * @param runner 사용자 엔티티
     */
    public static Authentication toAuthentication(Runner runner) {
        return new UsernamePasswordAuthenticationToken(
                runner.getId(),       // principal
                null,                 // credentials
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    /**
     * 사용자 ID를 가지고 Authentication 객체를 만듭니다.
     *
     * @param runnerId 사용자 ID
     */
    public static Authentication toAuthentication(Long runnerId) {
        return new UsernamePasswordAuthenticationToken(
                runnerId,               // principal
                null,                   // credentials
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}