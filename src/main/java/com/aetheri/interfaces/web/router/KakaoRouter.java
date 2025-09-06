package com.aetheri.interfaces.web.router;


import com.aetheri.interfaces.web.handler.KakaoHandler;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class KakaoRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/oauth2/authorization/kakao",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = KakaoHandler.class,
                    beanMethod = "kakaoLogin",   // Handler 메서드 이름과 일치해야 함
                    operation = @Operation(
                            operationId = "kakaoLogin",
                            summary = "kakaoLogin endpoint",
                            tags = {"kakao"}
                    )
            )
    })
    public RouterFunction<ServerResponse> kakaoLogin(KakaoHandler kakaoHandler) {
        return route(GET("/api/oauth2/authorization/kakao"), kakaoHandler::redirectToKakaoLogin);
    }

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/login/oauth2/code/kakao",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = KakaoHandler.class,
                    beanMethod = "kakaoRedirect",   // Handler 메서드 이름과 일치해야 함
                    operation = @Operation(
                            operationId = "kakaoRedirect",
                            summary = "kakaoRedirect endpoint",
                            tags = {"kakao"}
                    )
            )
    })
    public RouterFunction<ServerResponse> kakaoRedirect(KakaoHandler kakaoHandler) {
        return route(GET("/login/oauth2/code/kakao"), kakaoHandler::getKakaoAccessToken);
    }
}