package com.example.SomeOne.config.auth;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {

    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60; // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 14; // 14일

    private final JwtTokenProvider jwtTokenProvider;

    // Access Token 및 Refresh Token 생성
    public AuthTokens generate(String uid) {
        long now = (new Date()).getTime();
        Date accessTokenExpiryDate = new Date(now + ACCESS_TOKEN_EXPIRATION_TIME);
        Date refreshTokenExpiryDate = new Date(now + REFRESH_TOKEN_EXPIRATION_TIME);

        String accessToken = jwtTokenProvider.generateAccessToken(uid, accessTokenExpiryDate);
        String refreshToken = jwtTokenProvider.generateRefreshToken(refreshTokenExpiryDate);

        return AuthTokens.of(accessToken, refreshToken, BEARER_TYPE, ACCESS_TOKEN_EXPIRATION_TIME / 1000L);
    }
}
