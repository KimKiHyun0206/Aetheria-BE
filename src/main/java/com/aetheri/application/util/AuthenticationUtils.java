package com.aetheri.application.util;

import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
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

    public static Mono<String> validateNotBlankMono(String value, ErrorMessage error, String message) {
        return Mono.justOrEmpty(value)
                .filter(v -> !v.isBlank())
                .switchIfEmpty(Mono.error(new BusinessException(error, message)));
    }
}