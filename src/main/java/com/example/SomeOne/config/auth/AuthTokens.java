package com.example.SomeOne.config.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTokens {
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final long accessTokenExpiresIn;

    public static AuthTokens of(String accessToken, String refreshToken, String tokenType, long expiresIn) {
        return new AuthTokens(accessToken, refreshToken, tokenType, expiresIn);
    }
}
