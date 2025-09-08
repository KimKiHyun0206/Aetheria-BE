package com.aetheri.application.service.sign;

import com.aetheri.application.port.out.r2dbc.KakaoTokenRepositortyPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 회원 탈퇴 서비스
 * */
@Service
@RequiredArgsConstructor
public class SignOffService {
    private final KakaoTokenRepositortyPort kakaoTokenRepositortyPort;

    public Mono<Void> signOff(Long runnerId) {
        return kakaoTokenRepositortyPort.existByRunnerId(runnerId)
                .flatMap(exist -> {
                    if (exist) {
                        return kakaoTokenRepositortyPort.deleteByRunnerId(runnerId);
                    } else {
                        return Mono.empty();
                    }
                });
    }
}