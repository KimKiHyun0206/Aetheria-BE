package com.aetheri.application.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@UtilityClass
public class AuthenticationUtils {
    public static Mono<Long> extractRunnerIdFromRequest(ServerRequest request) {
        return request.principal()
                .cast(Authentication.class)
                .map(auth -> Long.valueOf(auth.getName()));
    }

    public static Mono<Authentication> extractAuthenticationFromRequest(ServerRequest request) {
        return request.principal()
                .cast(Authentication.class);
    }
}