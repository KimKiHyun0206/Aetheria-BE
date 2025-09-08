package com.aetheri.application.service;

import com.aetheri.application.port.out.r2dbc.RunnerRepositoryPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.persistence.Runner;
import com.aetheri.interfaces.dto.kakao.KakaoUserInfoResponseDto;
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
     * @param dto 카카오에서 받아온 사용자 정보
     * @implNote Void로 반환하는 이유는 회원가입하고 난 엔티티를 비즈니스 로직에서 다시 사용하지 않을 것이기 때문.
     * @implNote then을 사용하는 이유는 {@code Mono<Runner>}를 {@code Mono<Void>}로 변환하기 위함임.
     * */
    public Mono<Void> signUp(KakaoUserInfoResponseDto dto) {
        return runnerRepositoryPort.existByKakaoId(dto.id())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new BusinessException(
                                ErrorMessage.DUPLICATE_RUNNER,
                                "이미 존재하는 사용자입니다."
                        ));
                    }
                    Runner runner = new Runner(
                            dto.id(),
                            dto.properties().get("nickname")
                    );
                    return runnerRepositoryPort.save(runner).then();    // Mono<Void>로 변환하기 위해 then 사용
                });
    }
}