package com.aetheri.application.service;

import com.aetheri.application.dto.MeResponse;
import com.aetheri.application.port.out.r2dbc.RunnerRepositoryPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MePort {
    private final RunnerRepositoryPort runnerRepositoryPort;

    public Mono<MeResponse> me(Long id) {
        if (id == null || id <= 0) {
            return Mono.error(new BusinessException(
                    ErrorMessage.INVALID_REQUEST_PARAMETER,
                    "유효한 요청 파라미터가 아닙니다.")
            );
        }
        return runnerRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.NOT_FOUND_RUNNER,
                        "사용자를 찾을 수 없습니다."))
                )
                .map(MeResponse::of);
    }
}