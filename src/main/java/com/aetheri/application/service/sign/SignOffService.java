package com.aetheri.application.service.sign;

import com.aetheri.application.dto.KakaoTokenResponse;
import com.aetheri.application.port.out.kakao.KakaoRefreshTokenPort;
import com.aetheri.application.port.out.kakao.KakaoUnlinkPort;
import com.aetheri.application.port.out.r2dbc.KakaoTokenRepositortyPort;
import com.aetheri.application.port.out.r2dbc.RunnerRepositoryPort;
import com.aetheri.infrastructure.persistence.KakaoToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 회원 탈퇴 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignOffService {
    private final KakaoTokenRepositortyPort kakaoTokenRepositortyPort;
    private final KakaoUnlinkPort kakaoUnlinkPort;
    private final KakaoRefreshTokenPort kakaoRefreshTokenPort;
    private final RunnerRepositoryPort runnerRepositoryPort;

    /**
     * JWT 토큰에서 가져온 사용자의 ID를 사용하여 회원 탈퇴 처리
     *
     * @param runnerId 사용자 ID
     */
    public Mono<Void> signOff(Long runnerId) {
        return kakaoTokenRepositortyPort
                // 카카오 리프레쉬 토큰을 데이터베이스에서 조회
                .findByRunnerId(runnerId)
                // 리프레쉬 토큰을 갱신
                .flatMap(this::refreshKakaoToken)
                // 카카오에서 회원 탈퇴
                .flatMap(this::unlinkKakaoUser)
                // 데이터베이스에 저장된 카카오 토큰 삭제
                .then(deleteKakaoToken(runnerId))
                // 데이터베이스에 저장된 회원 정보 삭제
                .then(deleteRunner(runnerId))
                // 성공 시 로그 출력
                .doOnSuccess(v -> log.info("[SignOffService] 사용자 {}가 회원 탈퇴하였습니다.", runnerId));
    }


    private Mono<KakaoTokenResponse> refreshKakaoToken(KakaoToken kakaoToken) {
        return kakaoRefreshTokenPort
                .refreshAccessToken(kakaoToken.getRefreshToken());
    }

    private Mono<Void> unlinkKakaoUser(KakaoTokenResponse tokenResponse) {
        return kakaoUnlinkPort.unlink(tokenResponse.access_token()).then();
    }

    private Mono<Void> deleteKakaoToken(Long runnerId) {
        return kakaoTokenRepositortyPort.deleteByRunnerId(runnerId);
    }

    private Mono<Void> deleteRunner(Long runnerId) {
        return runnerRepositoryPort.deleteById(runnerId);
    }
}