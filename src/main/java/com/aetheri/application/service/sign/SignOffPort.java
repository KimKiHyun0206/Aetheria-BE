package com.aetheri.application.service.sign;

import com.aetheri.application.dto.KakaoTokenResponse;
import com.aetheri.application.port.in.sign.SignOffUseCase;
import com.aetheri.application.port.out.kakao.KakaoRefreshTokenPort;
import com.aetheri.application.port.out.kakao.KakaoUnlinkPort;
import com.aetheri.application.port.out.r2dbc.KakaoTokenRepositoryPort;
import com.aetheri.application.port.out.r2dbc.RunnerRepositoryPort;
import com.aetheri.application.port.out.redis.RedisRefreshTokenRepositoryPort;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import com.aetheri.infrastructure.persistence.entity.KakaoToken;
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
public class SignOffPort implements SignOffUseCase {
    private final KakaoTokenRepositoryPort kakaoTokenRepositoryPort;
    private final KakaoUnlinkPort kakaoUnlinkPort;
    private final KakaoRefreshTokenPort kakaoRefreshTokenPort;
    private final RunnerRepositoryPort runnerRepositoryPort;
    private final RedisRefreshTokenRepositoryPort redisRefreshTokenRepositoryPort;

    /**
     * JWT 토큰에서 가져온 사용자의 ID를 사용하여 회원 탈퇴 처리
     *
     * @param runnerId 사용자 ID
     */
    @Override
    public Mono<Void> signOff(Long runnerId) {
                // 데이터베이스에서 사용자 ID로 카카오 토큰을 찾습니다.
        return findKakaoToken(runnerId)
                // 리프레쉬 토큰을 갱신
                .flatMap(this::refreshKakaoToken)
                // 카카오에서 회원 탈퇴
                .flatMap(this::unlinkKakaoUser)
                // 데이터베이스에 저장된 카카오 토큰 삭제
                .then(deleteKakaoToken(runnerId))
                // Redis에서 리프레쉬 토큰 삭제
                .then(deleteRefreshTokenFromRedis(runnerId))
                // 데이터베이스에 저장된 회원 정보 삭제
                .then(deleteRunner(runnerId))
                // 성공 시 로그 출력
                .doOnSuccess(v -> log.info("[SignOffService] 사용자 {}가 회원 탈퇴하였습니다.", runnerId));
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
     * 회원 탈퇴 API를 사용하는 메소드
     *
     * @param tokenResponse 재발급한 카카오 토큰이 담긴 DTO
     * */
    private Mono<Void> unlinkKakaoUser(KakaoTokenResponse tokenResponse) {
        return kakaoUnlinkPort.unlink(tokenResponse.accessToken()).then();
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
     * 데이터베이스에서 사용자를 삭제하는 메소드
     *
     * @param runnerId 사용자 ID
     * */
    private Mono<Void> deleteRunner(Long runnerId) {
        return runnerRepositoryPort.deleteById(runnerId);
    }

    /**
     * 리프래쉬 토큰을 Redis에서 삭제하기 위한 메소드
     *
     * @param runnerId 사용자 ID
     * */
    private Mono<Void> deleteRefreshTokenFromRedis(Long runnerId) {
        return redisRefreshTokenRepositoryPort.deleteRefreshToken(runnerId).then();
    }
}