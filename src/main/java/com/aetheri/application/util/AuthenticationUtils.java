package com.aetheri.application.util;

import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * 사용자가 요청한 ServerRequest에서 권한을 가져오기 위한 유틸
 * */
@UtilityClass
public class AuthenticationUtils {
    /**
     * ServerRequest에서 사용자 ID를 가져오기 위한 메소드
     *
     * @param request 사용자 요청
     * */
    public static Mono<Long> extractRunnerIdFromRequest(ServerRequest request) {
        return request.principal()
                .ofType(Authentication.class)
                .flatMap(auth -> {
                    try {
                        return Mono.just(Long.parseLong(auth.getName()));
                    } catch (NumberFormatException e) {
                        return Mono.error(new BusinessException(
                                ErrorMessage.JWT_SUBJECT_IS_NOT_NUMBER,
                                "JWT subject가 숫자가 아닙니다."));
                    }
                })
                .switchIfEmpty(Mono.empty());
    }

    /**
     * ServerRequest에서 Authentication를 가져오기 위한 메소드
     *
     * @param request 사용자 요청
     * */
    public static Mono<Authentication> extractAuthenticationFromRequest(ServerRequest request) {
        return request.principal()
                .cast(Authentication.class);
    }
}