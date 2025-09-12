package com.aetheri.application.service.sign;

import com.aetheri.application.port.out.r2dbc.RunnerRepositoryPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.persistence.Runner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignUpService {
    private final RunnerRepositoryPort runnerRepositoryPort;

    /**
     * 서버 측의 회원가입을 위한 메소드.
     *
     * @param id 사용자의 ID
     * @param name 사용자의 이름
     * @implNote Void로 반환하는 이유는 회원가입하고 난 엔티티를 비즈니스 로직에서 다시 사용하지 않을 것이기 때문.
     * @implNote then을 사용하는 이유는 {@code Mono<Runner>}를 {@code Mono<Void>}로 변환하기 위함임.
     * */
    public Mono<Void> signUp(Long id, String name) {
        // 1. 카카오 ID로 사용자가 존재하는지 확인
        return runnerRepositoryPort.existsByKakaoId(id)
                // 2. 만약 이미 존재한다면, (true -> false)가 되어 스트림이 비어버림
                .filter(isExist -> !isExist)
                // 3. 스트림이 비었을 때 (사용자가 존재할 때), 에러 발생
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.DUPLICATE_RUNNER,
                        "이미 존재하는 사용자입니다."
                )))
                // 4. 스트림이 비어있지 않을 때 (사용자가 존재하지 않을 때), 회원가입 진행
                .flatMap(notExists -> {
                    Runner runner = new Runner(
                            id,
                            name
                    );
                    return runnerRepositoryPort.save(runner);
                })
                .then(); // Mono<Void>로 변환
    }
}