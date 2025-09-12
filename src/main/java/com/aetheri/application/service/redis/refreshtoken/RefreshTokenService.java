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

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisRefreshTokenRepositoryPort redisRefreshTokenRepositoryPort;
    private final JwtTokenProviderPort jwtTokenProviderPort;
    private final JwtTokenResolverPort jwtTokenResolverPort;

    public Mono<TokenResponse> refreshToken(Long runnerId) {
        return findRefreshTokenFromRedis(runnerId)
                .flatMap(this::reissueTokens);
    }

    public Mono<TokenResponse> reissueTokens(String refreshToken) {
        return Mono.fromCallable(() -> jwtTokenResolverPort.getIdFromToken(refreshToken))
                .map(AuthenticationConverter::toAuthentication)
                .flatMap(this::deleteAndCreateTokens);
    }

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

    private Mono<TokenResponse> deleteAndCreateTokens(Authentication auth) {
        Long runnerId = Long.valueOf(auth.getName());
        return redisRefreshTokenRepositoryPort
                .deleteRefreshToken(runnerId)
                .then(Mono.fromSupplier(() -> createTokenResponse(auth)));
    }

    private TokenResponse createTokenResponse(Authentication authentication) {
        String accessToken = jwtTokenProviderPort.generateAccessToken(authentication);
        RefreshTokenIssueResponse refreshToken = jwtTokenProviderPort.generateRefreshToken(authentication);
        return TokenResponse.of(accessToken, refreshToken);
    }
}