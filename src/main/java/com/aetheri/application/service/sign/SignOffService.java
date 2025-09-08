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

    /**
     * JWT 토큰에서 가져온 사용자의 ID를 사용하여 삭제함
     *
     * @param runnerId JWT 토큰에서 가져온 사용자의 ID
     * */
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