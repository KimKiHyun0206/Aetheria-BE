package com.aetheri.application.service.sign;

import com.aetheri.application.dto.KakaoTokenResponse;
import com.aetheri.application.port.out.kakao.KakaoLogoutPort;
import com.aetheri.application.port.out.kakao.KakaoRefreshTokenPort;
import com.aetheri.application.port.out.r2dbc.KakaoTokenRepositoryPort;
import com.aetheri.application.port.out.redis.RedisRefreshTokenRepositoryPort;
import com.aetheri.infrastructure.persistence.KakaoToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 로그아웃 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignOutService {
    private final KakaoTokenRepositoryPort kakaoTokenRepositoryPort;
    private final KakaoRefreshTokenPort kakaoRefreshTokenPort;
    private final KakaoLogoutPort kakaoLogoutPort;
    private final RedisRefreshTokenRepositoryPort redisRefreshTokenRepositoryPort;

    public Mono<Void> signOut(Long runnerId) {
        return kakaoTokenRepositoryPort.findByRunnerId(runnerId)
                .flatMap(this::refreshKakaoToken)
                .flatMap(this::kakaoLogout)
                .then(deleteKakaoToken(runnerId))
                .then(deleteRefreshToken(runnerId))
                .doOnSuccess(v -> log.info("[SignOutService] 로그아웃 성공: {}", runnerId));
    }

    private Mono<KakaoTokenResponse> refreshKakaoToken(KakaoToken kakaoToken) {
        return kakaoRefreshTokenPort
                .refreshAccessToken(kakaoToken.getRefreshToken());
    }

    private Mono<Void> kakaoLogout(KakaoTokenResponse kakaoTokenResponse) {
        return kakaoLogoutPort.logout(kakaoTokenResponse.access_token());
    }

    private Mono<Void> deleteKakaoToken(Long runnerId) {
        return kakaoTokenRepositoryPort.deleteByRunnerId(runnerId);
    }

    private Mono<Void> deleteRefreshToken(Long runnerId) {
        return redisRefreshTokenRepositoryPort.deleteRefreshToken(runnerId).then();
    }
}