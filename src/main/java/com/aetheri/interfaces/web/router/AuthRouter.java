package com.aetheri.interfaces.web.router;


import com.aetheri.interfaces.web.handler.AuthHandler;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Slf4j
@Configuration
public class AuthRouter {

    @Bean
    @RouterOperations({
            // 1. 카카오 로그인 페이지 리다이렉트
            @RouterOperation(
                    path = "/api/v1/auth/authorization/kakao",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = AuthHandler.class,
                    beanMethod = "redirectToKakaoLogin",
                    operation = @Operation(
                            operationId = "authRedirectToKakao",
                            summary = "카카오 로그인 페이지로 리다이렉트",
                            tags = {"Auth"}
                    )
            ),
            // 2. 로그인 및 토큰 발급
            @RouterOperation(
                    path = "/api/v1/auth/sign-in",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = AuthHandler.class,
                    beanMethod = "getKakaoAccessToken",
                    operation = @Operation(
                            operationId = "authSignIn",
                            summary = "로그인 코드로 카카오 액세스 토큰 발급",
                            tags = {"Auth"}
                    )
            ),
            // 3. 회원 탈퇴
            @RouterOperation(
                    path = "/api/v1/auth/sign-off",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "signOff",
                    operation = @Operation(
                            operationId = "authSignOff",
                            summary = "카카오 회원 탈퇴",
                            tags = {"Auth"}
                    )
            ),
            // 4. 로그아웃
            @RouterOperation(
                    path = "/api/v1/auth/sign-out",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "signOut",
                    operation = @Operation(
                            operationId = "authSignOut",
                            summary = "카카오 로그아웃",
                            tags = {"Auth"}
                    )
            )
    })
    public RouterFunction<ServerResponse> authRoutes(AuthHandler authHandler) {
        return RouterFunctions.route()
                .path("/api/v1/auth", builder -> builder
                        .GET("/authorization/kakao", authHandler::redirectToKakaoLogin)
                        .GET("/sign-in", authHandler::getKakaoAccessToken)
                        .POST("/sign-off", authHandler::signOff)
                        .POST("/sign-out", authHandler::signOut))
                .build();
    }
}
