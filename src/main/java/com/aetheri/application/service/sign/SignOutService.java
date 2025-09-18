package com.aetheri.application.service.sign;

import com.aetheri.application.dto.KakaoTokenResponse;
import com.aetheri.application.port.in.sign.SignOutPort;
import com.aetheri.application.port.out.kakao.KakaoLogoutPort;
import com.aetheri.application.port.out.kakao.KakaoRefreshTokenPort;
import com.aetheri.application.port.out.r2dbc.KakaoTokenRepositoryPort;
import com.aetheri.application.port.out.redis.RedisRefreshTokenRepositoryPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
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
public class SignOutService implements SignOutPort {
    private final KakaoTokenRepositoryPort kakaoTokenRepositoryPort;
    private final KakaoRefreshTokenPort kakaoRefreshTokenPort;
    private final KakaoLogoutPort kakaoLogoutPort;
    private final RedisRefreshTokenRepositoryPort redisRefreshTokenRepositoryPort;

    /**
     * 회원 탈퇴를 위한 메소드
     *
     * @param runnerId 탈퇴할 사용자의 ID
     * @implNote 만약 카카오에서 오류가 나도 서버에서는 로그아웃을 성공하도록 구현함.
     * */
    @Override
    public Mono<Void> signOut(Long runnerId) {
                // 데이터베이스에서 카카오 토큰을 조회합니다.
        return findKakaoToken(runnerId)
                // 리프래쉬 토큰을 재발급 받습니다
                .flatMap(this::refreshKakaoToken)
                // 카카오 로그아웃 API에 요청을 보냅니다.
                .flatMap(this::kakaoLogout)
                // 만약 로그아웃 요청이 제대로 이루어지지 않아도 서버에서 로그아웃이 성공하도록 합니다.
                .onErrorResume(ex -> {
                    log.warn("[SignOutService] 카카오 API 로그아웃 실패. runnerId={}, cause={}", runnerId, ex.toString());
                    return Mono.empty(); // 로컬 정리 계속
                })
                // 데이터베이스의 카카오 토큰을 삭제합니다
                .then(deleteKakaoToken(runnerId))
                // Redis의 리프래쉬 토큰을 삭제합니다
                .then(deleteRefreshToken(runnerId))
                // 성공햇다면 성공을 로깅합니다.
                .doOnSuccess(v -> log.info("[SignOutService] 로그아웃 성공: {}", runnerId));
    }

    /**
     * 데이터베이스에서 사용자 ID를 사용해 카카오 토큰을 찾습니다.
     *
     * @param runnerId 사용자 ID
     * @implNote 카카오 토큰이 없을 경우 예외를 발생시킵니다.
     * */
    private Mono<KakaoToken> findKakaoToken(Long runnerId) {
        return kakaoTokenRepositoryPort.findByRunnerId(runnerId)
                .switchIfEmpty(Mono.error(new BusinessException(
                        ErrorMessage.NOT_FOUND_KAKAO_TOKEN,
                        "카카오 토큰을 찾을 수 없습니다."))
                );
    }

    /**
     * 카카오 토큰을 재발급받습니다.
     *
     * @param kakaoToken 재발급에 사용할 리프레쉬 토큰이 담긴 객체
     * */
    private Mono<KakaoTokenResponse> refreshKakaoToken(KakaoToken kakaoToken) {
        return kakaoRefreshTokenPort
                .refreshAccessToken(kakaoToken.getRefreshToken());
    }

    /**
     * 카카오 API를 사용해 카카오 로그아웃을 진행하는 메소드
     *
     * @param kakaoTokenResponse 재발급받은 액세스 토큰이 있는 DTO
     * */
    private Mono<Void> kakaoLogout(KakaoTokenResponse kakaoTokenResponse) {
        return kakaoLogoutPort.logout(kakaoTokenResponse.accessToken());
    }

    /**
     * 데이터베이스에서 카카오 토큰을 삭제하는 메소드
     *
     * @param runnerId 사용자 ID
     * */
    private Mono<Void> deleteKakaoToken(Long runnerId) {
        return kakaoTokenRepositoryPort.deleteByRunnerId(runnerId);
    }

    /**
     * 리프래쉬 토큰을 Redis에서 삭제하기 위한 메소드
     *
     * @param runnerId 사용자 ID
     * */
    private Mono<Void> deleteRefreshToken(Long runnerId) {
        return redisRefreshTokenRepositoryPort.deleteRefreshToken(runnerId).then();
    }
}