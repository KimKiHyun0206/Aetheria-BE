package com.aetheri.interfaces.web.handler;

import com.aetheri.application.port.in.imgfetch.ImageFetchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImgFetchHandler {
    private final ImageFetchUseCase imageFetchUseCase;

    public Mono<ServerResponse> imageFetch(ServerRequest serverRequest) {
        String path = serverRequest.pathVariable("path");
        Mono<Resource> imageMono = imageFetchUseCase.fetchImage(path);

        return ServerResponse.ok()
                // 이미지 타입에 맞게 MediaType 설정 (예: JPEG)
                .contentType(MediaType.IMAGE_JPEG)
                // body()를 사용하여 Mono<T>를 응답 본문으로 스트리밍
                .body(imageMono, Resource.class);
    }
}