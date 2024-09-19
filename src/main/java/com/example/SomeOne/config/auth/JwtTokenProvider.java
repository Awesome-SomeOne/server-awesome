package com.example.SomeOne.config.auth;

import com.example.SomeOne.service.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtTokenProvider(@Value("${custom.jwt.secretKey}") String secretKey, CustomUserDetailsService customUserDetailsService) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);  // Base64 디코딩
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);  // SecretKey 생성
        this.customUserDetailsService = customUserDetailsService;
    }

    // Access Token 생성
    public String generateAccessToken(String uid, Date expiryDate) {
        return Jwts.builder()
                .setSubject(uid)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(Date expiryDate) {
        return Jwts.builder()
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // JWT에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰 검증 후 인증 정보 생성
    public Authentication getAuthentication(String userId) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
