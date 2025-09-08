package com.aetheri.application.service;

import com.aetheri.application.dto.MeResponse;
import com.aetheri.application.port.out.r2dbc.RunnerRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MeService {
    private final RunnerRepositoryPort runnerRepositoryPort;

    public Mono<MeResponse> me(Long id) {
        return runnerRepositoryPort.findById(id)
                .map(MeResponse::of);
    }
}