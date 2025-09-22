package com.aetheri.interfaces.web.router;

import com.aetheri.application.dto.image.ImageMetadataResponse;
import com.aetheri.application.dto.image.ImageMetadataSaveRequest;
import com.aetheri.application.dto.image.ImageMetadataUpdateRequest;
import com.aetheri.interfaces.web.handler.ImageMetadataHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Slf4j
@Configuration
public class ImageMetadataRouter {
    @Bean
    @RouterOperations({
            // GET 요청에 대한 API 문서 정의
            @RouterOperation(
                    path = "/api/v1/image/{imageId}",
                    method = RequestMethod.GET,
                    beanClass = ImageMetadataHandler.class,
                    beanMethod = "findImage",
                    operation = @Operation(
                            operationId = "findImageById",
                            summary = "ID로 이미지 메타데이터를 조회합니다.",
                            parameters = {
                                    @Parameter(
                                            name = "imageId",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "조회할 이미지의 고유 ID"
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "정상적으로 이미지 메타데이터를 반환합니다.",
                                            content = @Content(schema = @Schema(implementation = String.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "해당 ID의 이미지를 찾을 수 없습니다."
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/image",
                    method = RequestMethod.GET,
                    beanClass = ImageMetadataHandler.class,
                    beanMethod = "findImagesByRunnerId",
                    operation = @Operation(
                            operationId = "findImagesByRunnerId",
                            summary = "사용자 ID로 이미지 메타데이터를 조회합니다.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "정상적으로 이미지 메타데이터를 반환합니다.",
                                            content = @Content(schema = @Schema(implementation = String.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "해당 ID의 이미지를 찾을 수 없습니다."
                                    )
                            }
                    )
            ),
            // DELETE 요청에 대한 API 문서 정의
            @RouterOperation(
                    path = "/api/v1/image/{imageId}",
                    method = RequestMethod.DELETE,
                    beanClass = ImageMetadataHandler.class,
                    beanMethod = "deleteImage",
                    operation = @Operation(
                            operationId = "deleteImageById",
                            summary = "ID로 이미지를 삭제합니다.",
                            parameters = {
                                    // imageId를 pathVariable로 정의
                                    @Parameter(
                                            name = "imageId",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "삭제할 이미지의 고유 ID"
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "성공적으로 이미지를 삭제했습니다."
                                    )
                            }
                    )
            ),
            // PUT 요청에 대한 API 문서 정의
            @RouterOperation(
                    path = "/api/v1/image/{imageId}",
                    method = RequestMethod.PUT,
                    beanClass = ImageMetadataHandler.class,
                    beanMethod = "updateImage",
                    operation = @Operation(
                            operationId = "updateImageById",
                            summary = "ID로 이미지 메타데이터를 업데이트합니다.",
                            parameters = {
                                    @Parameter(
                                            name = "imageId",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "업데이트할 이미지의 고유 ID"
                                    )
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "업데이트할 이미지 메타데이터",
                                    content = @Content(
                                            schema = @Schema(implementation = ImageMetadataUpdateRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "성공적으로 이미지 메타데이터를 업데이트했습니다."
                                    )
                            }
                    )
            ),
            // POST 요청에 대한 API 문서 정의
            @RouterOperation(
                    path = "/api/v1/image",
                    method = RequestMethod.POST,
                    beanClass = ImageMetadataHandler.class,
                    beanMethod = "saveImage",
                    operation = @Operation(
                            operationId = "saveImage",
                            summary = "새로운 이미지 메타데이터를 저장합니다.",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "저장할 이미지 메타데이터",
                                    content = @Content(
                                            schema = @Schema(implementation = ImageMetadataSaveRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "성공적으로 이미지를 저장했습니다."
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> imageRoutes(ImageMetadataHandler handler) {
        return RouterFunctions.route()
                .path("/api/v1/image", builder -> builder
                        .POST("", handler::saveImage)
                        .GET("", request -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_EVENT_STREAM)
                                .body(handler.findImagesByRunnerId(request), ServerSentEvent.class)
                        )
                        .GET("/{imageId}", handler::findImage)
                        .DELETE("/{imageId}", handler::deleteImage)
                        .PUT("/{imageId}", handler::updateImage)
                )
                .build();
    }
}