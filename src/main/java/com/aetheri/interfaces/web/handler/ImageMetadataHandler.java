package com.aetheri.interfaces.web.handler;

import com.aetheri.application.dto.image.ImageMetadataResponse;
import com.aetheri.application.dto.image.ImageMetadataSaveRequest;
import com.aetheri.application.dto.image.ImageMetadataUpdateRequest;
import com.aetheri.application.port.in.image.DeleteImageMetadataUseCase;
import com.aetheri.application.port.in.image.FindImageMetadataUseCase;
import com.aetheri.application.port.in.image.SaveImageMetadataUseCase;
import com.aetheri.application.port.in.image.UpdateImageMetadataUseCase;
import com.aetheri.application.util.AuthenticationUtils;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageMetadataHandler {
    private final FindImageMetadataUseCase findImageMetadataUseCase;
    private final DeleteImageMetadataUseCase deleteImageMetadataUseCase;
    private final SaveImageMetadataUseCase saveImageMetadataUseCase;
    private final UpdateImageMetadataUseCase updateImageMetadataUseCase;
    private final Jackson2JsonEncoder encoder = new Jackson2JsonEncoder(new ObjectMapper(), MediaType.APPLICATION_JSON);
    private final DefaultDataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
    private final ResolvableType elementType = ResolvableType.forClass(ImageMetadataResponse.class);


    public Mono<ServerResponse> findImage(ServerRequest request) {
        Long imageId = Long.parseLong(request.pathVariable("imageId"));
        return AuthenticationUtils.extractRunnerIdFromRequest(request)
                .flatMap(runnerId -> findImageMetadataUseCase.findImageMetadataById(runnerId, imageId))
                .switchIfEmpty(findImageMetadataUseCase.findImageMetadataById(imageId))
                .flatMap(image -> ServerResponse.ok().bodyValue(image));
    }

    public Flux<ServerSentEvent<DataBuffer>> findImagesByRunnerId(ServerRequest request) {
        return AuthenticationUtils.extractRunnerIdFromRequest(request)
                .flatMapMany(findImageMetadataUseCase::findImageMetadataByRunnerId)
                .flatMap(response -> encoder.encode(
                                Flux.just(response),
                                dataBufferFactory,
                                elementType,
                                MediaType.APPLICATION_JSON,
                                Collections.emptyMap()
                        ).cast(DataBuffer.class)
                )
                .map(dataBuffer -> ServerSentEvent.<DataBuffer>builder()
                        .data(dataBuffer)
                        .build());

    }

    public Mono<ServerResponse> saveImage(ServerRequest request) {
        return AuthenticationUtils.extractRunnerIdFromRequest(request)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorMessage.FORBIDDEN, "유효한 인증 없이 접근할 수 없습니다.")))
                .flatMap(requestId ->
                        request.bodyToMono(ImageMetadataSaveRequest.class)
                                .flatMap(dto -> saveImageMetadataUseCase.saveImageMetadata(requestId, dto))
                ).flatMap(image -> ServerResponse.ok().bodyValue(image));
    }

    public Mono<ServerResponse> deleteImage(ServerRequest request) {
        Long imageId = Long.parseLong(request.pathVariable("imageId"));
        return AuthenticationUtils.extractRunnerIdFromRequest(request)
                .flatMap(runnerId -> deleteImageMetadataUseCase.deleteImageMetadata(runnerId, imageId))
                .flatMap(image -> ServerResponse.ok().bodyValue(image));
    }

    public Mono<ServerResponse> updateImage(ServerRequest request) {
        Long imageId = Long.parseLong(request.pathVariable("imageId"));
        return AuthenticationUtils.extractRunnerIdFromRequest(request)
                .flatMap(runnerId ->
                        request.bodyToMono(ImageMetadataUpdateRequest.class)
                                .flatMap(dto -> updateImageMetadataUseCase.updateImageMetadata(runnerId, imageId, dto))
                )
                .flatMap(image -> ServerResponse.ok().bodyValue(image));
    }
}
