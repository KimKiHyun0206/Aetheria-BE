package com.aetheri.application.service.redis.refreshtoken;

import com.aetheri.application.dto.jwt.RefreshTokenIssueResponse;
import com.aetheri.application.dto.jwt.TokenResponse;
import com.aetheri.application.port.out.jwt.JwtTokenProviderPort;
import com.aetheri.application.port.out.jwt.JwtTokenResolverPort;
import com.aetheri.application.port.out.redis.RedisRefreshTokenRepositoryPort;
import com.aetheri.application.service.converter.AuthenticationConverter;
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
        return redisRefreshTokenRepositoryPort
                .getRefreshToken(runnerId)
                .flatMap(this::reissueTokens);
    }

    public Mono<TokenResponse> reissueTokens(String refreshToken) {
        Long runnerId = jwtTokenResolverPort.getIdFromToken(refreshToken);
        Authentication authentication = AuthenticationConverter.toAuthentication(runnerId);

        return redisRefreshTokenRepositoryPort
                .deleteRefreshToken(runnerId)
                .then(Mono.fromSupplier(() -> createTokenResponse(authentication)));
    }

    private TokenResponse createTokenResponse(Authentication authentication) {
        String accessToken = jwtTokenProviderPort.generateAccessToken(authentication);
        RefreshTokenIssueResponse refreshToken = jwtTokenProviderPort.generateRefreshToken(authentication);
        return TokenResponse.of(accessToken, refreshToken);
    }

}