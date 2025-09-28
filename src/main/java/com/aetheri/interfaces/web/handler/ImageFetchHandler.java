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
public class ImageFetchHandler {
    private final ImageFetchUseCase imageFetchUseCase;

    public Mono<ServerResponse> imageFetch(ServerRequest serverRequest) {
        String path = serverRequest.pathVariable("path");
        Mono<Resource> imageMono = imageFetchUseCase.fetchImage(path);

        return ServerResponse.ok()
                // 이미지 타입에 맞게 MediaType 설정
                // TODO 추후 비트맵 이미지를 저장하고 통신할 수 있도록 함
                .contentType(MediaType.IMAGE_JPEG)
                // 1시간 동안 이미지를 캐싱할 수 있도록 함.
                .header("Cache-Control", "public, max-age=3600") // 1시간 캐시
                // body()를 사용하여 Mono<T>를 응답 본문으로 스트리밍
                .body(imageMono, Resource.class);
    }
}