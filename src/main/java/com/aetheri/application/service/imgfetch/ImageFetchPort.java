package com.aetheri.application.service.imgfetch;

import com.aetheri.application.port.in.imgfetch.ImageFetchUseCase;
import com.aetheri.application.port.in.imgfetch.ImagePathValidateUseCase;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.config.properties.ImageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Paths;

@Slf4j
@Service
public class ImageFetchPort implements ImageFetchUseCase {
    private final ResourceLoader resourceLoader;
    private final ImagePathValidateUseCase imagePathValidator;
    private final String locationPattern;

    public ImageFetchPort(
            ResourceLoader resourceLoader,
            ImagePathValidateUseCase imagePathValidator,
            ImageProperties imageProperties
    ) {
        this.resourceLoader = resourceLoader;
        this.imagePathValidator = imagePathValidator;
        this.locationPattern = imageProperties.path();
    }

    @Override
    public Mono<Resource> fetchImage(String path) {
        String fileName = Paths.get(path).getFileName().toString();

        return imagePathValidator.isValidatePath(fileName)
                .flatMap(valid -> {
                    if (!valid) {
                        return Mono.error(
                                new BusinessException(
                                        ErrorMessage.INVALID_IMAGE_PATH,
                                        "유효한 이미지 요청이 아닙니다."
                                )
                        );
                    }
                    return Mono.just(resourceLoader.getResource(locationPattern + fileName));
                })
                .switchIfEmpty(
                        Mono.error(
                                new BusinessException(
                                        ErrorMessage.NOT_FOUND_IMAGE,
                                        "이미지를 조회하지 못했습니다."
                                ))
                )
                .doOnSuccess(l -> log.info("이미지 조회에 성공했습니다."));
    }
}