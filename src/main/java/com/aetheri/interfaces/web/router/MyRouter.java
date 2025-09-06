package com.aetheri.interfaces.web.router;

import com.aetheri.interfaces.web.handler.MyHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class MyRouter {


    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/hello",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = MyHandler.class,
                    beanMethod = "hello",   // Handler 메서드 이름과 일치해야 함
                    operation = @Operation(
                            operationId = "hello",
                            summary = "Hello endpoint",
                            tags = {"Hello"},
                            /*requestBody = @RequestBody(   // 예시용으로 넣어둔 것 여기에서는 실제로 사용하지 않음
                                    required = true,
                                    description = "Hello request payload",
                                    content = @Content(
                                            schema = @Schema(implementation = String.class)
                                    )
                            ),*/
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK",
                                            content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "404", description = "Not Found")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> myRoute(MyHandler myHandler) {
        return route(GET("/api/hello"), myHandler::hello);
    }
}