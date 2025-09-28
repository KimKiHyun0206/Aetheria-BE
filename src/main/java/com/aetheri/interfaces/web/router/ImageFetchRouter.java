package com.aetheri.interfaces.web.router;

import com.aetheri.interfaces.web.handler.ImageFetchHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ImageFetchRouter {

    @Bean
    @RouterOperation(
            path = "/api/v1/running-art/{path}",
            method = RequestMethod.GET,
            beanClass = ImageFetchHandler.class,
            beanMethod = "imageFetch",
            operation = @Operation(
                    operationId = "fetchRunnerArtImage",
                    summary = "경로 변수를 사용하여 달리기 기록 아트 이미지 가져오기",
                    parameters = {
                            @Parameter(
                                    in = ParameterIn.PATH,
                                    name = "path",
                                    description = "이미지 파일명 (확장자 포함)",
                                    required = true,
                                    example = "sample_image.png"
                            )
                    },
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "이미지가 성공적으로 반환됨 (스트리밍)",
                                    content = @Content(
                                            mediaType = MediaType.IMAGE_JPEG_VALUE,
                                            // 응답이 파일(바이너리)임을 스키마로 명시합니다.
                                            schema = @Schema(type = "string", format = "binary")
                                    )
                            ),
                            @ApiResponse(
                                    responseCode = "404",
                                    description = "해당 경로의 이미지를 찾을 수 없음"
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> imageFetchRoutes(ImageFetchHandler imageFetchHandler) {
        return RouterFunctions.route()
                .GET("/api/v1/running-art/{path}", imageFetchHandler::imageFetch)
                .build();
    }
}