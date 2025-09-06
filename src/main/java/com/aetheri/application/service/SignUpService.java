package com.aetheri.application.service;

import com.aetheri.application.port.out.r2dbc.RunnerRepositoryPort;
import com.aetheri.infrastructure.persistence.Runner;
import com.aetheri.interfaces.dto.kakao.KakaoUserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final RunnerRepositoryPort runnerRepositoryPort;

    public Mono<Runner> signUp(KakaoUserInfoResponseDto dto) {
        Runner runner = new Runner(dto.id(), dto.kakaoAccount().name());
        return runnerRepositoryPort.save(runner);
    }
}