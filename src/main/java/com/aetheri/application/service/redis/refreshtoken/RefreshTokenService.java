package com.aetheri.application.service.redis.refreshtoken;

import com.aetheri.application.dto.jwt.RefreshTokenIssueResponse;
import com.aetheri.application.dto.jwt.TokenResponse;
import com.aetheri.application.port.out.jwt.JwtTokenProviderPort;
import com.aetheri.application.port.out.jwt.JwtTokenResolverPort;
import com.aetheri.application.port.out.redis.RedisRefreshTokenRepositoryPort;
import com.aetheri.application.service.converter.AuthenticationConverter;
import com.aetheri.domain.exception.BusinessException;
import com.aetheri.domain.exception.message.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 리프래쉬 토큰을 재발급해주는 서비스입니다.
 *
 * @see RedisRefreshTokenRepositoryPort     Redis에 저장된 리프래쉬 토큰에 접근하기 위한 포트입니다
 * @see JwtTokenProviderPort                JWT 토큰을 다시 발급하기 위한 포트입니다
 * @see JwtTokenResolverPort                JWT 토큰 내용을 추출하기 위한 포트입니다
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisRefreshTokenRepositoryPort redisRefreshTokenRepositoryPort;
    private final JwtTokenProviderPort jwtTokenProviderPort;
    private final JwtTokenResolverPort jwtTokenResolverPort;

    /**
     * 사용자의 ID로 토큰을 재발급합니다.
     *
     * @param runnerId 사용자의 ID
     */
    public Mono<TokenResponse> refreshToken(Long runnerId) {
        // Redis에서 리프래쉬 토큰을 찾습니다.
        return findRefreshTokenFromRedis(runnerId)
                // 찾아온 리프래쉬 토큰을 사용하여 토큰을 재발급합니다.
                .flatMap(this::reissueTokens);
    }

    /**
     * 리프래쉬 토큰을 사용하여 토큰을 재발급합니다.
     *
     * @param refreshToken 재발급에 사용될 리프래쉬 토큰
     */
    public Mono<TokenResponse> reissueTokens(String refreshToken) {
        // 리프래쉬 토큰에서 사용자 ID를 추출합니다.
        return extractRunnerIdReactive(refreshToken)
                // 사용자 ID를 사용해서 Authentication을 만듭니다.
                .map(AuthenticationConverter::toAuthentication)
                // 만들어진 Authentication으로 기존의 토큰을 삭제하고 새로 만듭니다.
                .flatMap(this::deleteOldTokenAndCreateNew);
    }

    /**
     * 리프래쉬 토큰에서 사용자 ID를 추출합니다.
     *
     * @param refreshToken 추출에 사용될 리프래쉬 토큰
     * @implNote 추출은 블로킬 호출이기 때문에 별도의 스레드에서 진행시킵니다.
     */
    private Mono<Long> extractRunnerIdReactive(String refreshToken) {
        return Mono.fromCallable(() -> jwtTokenResolverPort.getIdFromToken(refreshToken))
                .subscribeOn(Schedulers.boundedElastic()); // 블로킹 호출을 별도 스레드풀에서
    }

    /**
     * 기존의 토큰을 삭제하고 새로운 토큰을 생성합니다.
     *
     * @param auth 토큰 재발급에 사용될 Authentication
     */
    private Mono<TokenResponse> deleteOldTokenAndCreateNew(Authentication auth) {
        Long runnerId = Long.valueOf(auth.getName());
        return redisRefreshTokenRepositoryPort
                // 기존의 리프래쉬 토큰을 삭제합니다.
                .deleteRefreshToken(runnerId)
                // 새로운 리프래쉬 토큰을 생성합니다.
                .then(Mono.defer(() -> createAndSaveRefreshToken(auth, runnerId)));
    }

    /**
     * 사용자 ID를 사용하여 Redis에서 리프래쉬 토큰을 찾습니다.
     *
     * @param id 사용자의 ID
     * @implNote Redis에 저장된 리프래쉬 토큰을 찾을 수 없을 경우 BusinessException을 발생시킵니다.
     */
    private Mono<String> findRefreshTokenFromRedis(Long id) {
        return redisRefreshTokenRepositoryPort
                .getRefreshToken(id)
                .switchIfEmpty(
                        Mono.error(new BusinessException(
                                ErrorMessage.NOT_FOUND_REFRESH_TOKEN_IN_REDIS,
                                "리프레쉬 토큰을 찾을 수 없습니다.")
                        )
                );
    }

    /**
     * 리프래쉬 토큰을 생성하고 저장합니다.
     *
     * @param runnerId       사용자의 ID
     * @param authentication 사용자의 Authentication
     * @implNote 생성된 리프래쉬 토큰을 저장하고, 새로운 토큰을 생성합니다.
     */
    private Mono<TokenResponse> createAndSaveRefreshToken(Authentication authentication, Long runnerId) {
        String accessToken = jwtTokenProviderPort.generateAccessToken(authentication);
        RefreshTokenIssueResponse refreshToken = jwtTokenProviderPort.generateRefreshToken(authentication);
        return redisRefreshTokenRepositoryPort
                .saveRefreshToken(runnerId, refreshToken.refreshToken())
                .thenReturn(TokenResponse.of(accessToken, refreshToken));
    }
}