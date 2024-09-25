package com.example.SomeOne.controller;


import com.example.SomeOne.config.auth.JwtTokenProvider;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.dto.Login.Response.*;
import com.example.SomeOne.repository.UserRepository;
import com.example.SomeOne.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/get-access-token")
    public ResponseEntity<String> getAccessToken(@RequestParam String code) {
        // 카카오 인가 코드를 사용해 액세스 토큰 발급
        String accessToken = userService.getAccessTokenFromKakao(code);
        return ResponseEntity.ok(accessToken);
    }

    // 액세스 토큰을 통해 JWT 토큰 발급 API
    @PostMapping("/issue-jwt-token")
    public ResponseEntity<LoginResponse> issueJwtToken(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam String nickname) {
        // "Bearer " 접두어 제거
        accessToken = accessToken.replace("Bearer ", "");
        LoginResponse loginResponse = userService.issueJwtToken(accessToken, nickname);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam(value = "code") String code) {
        // 1. 인가 코드로 액세스 토큰 발급
        String accessToken = userService.getAccessTokenFromKakao(code);

        // 2. 액세스 토큰 반환
        return ResponseEntity.ok("Kakao Access Token: " + accessToken);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken) {
        userService.logoutFromKakao(accessToken);
        return ResponseEntity.ok("Logged out successfully.");
    }

    // 회원탈퇴
    @PostMapping("/unlink")
    public ResponseEntity<String> unlinkKakaoUser(@RequestHeader("Authorization") String accessToken) {
        userService.unlinkKakaoUser(accessToken);
        return ResponseEntity.ok("Kakao account unlinked successfully.");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 리프레시 토큰에서 사용자 ID 추출
        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        Users user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, new Date(System.currentTimeMillis() + 3600000)); // 1시간 유효

        // 새로운 액세스 토큰 응답
        return ResponseEntity.ok(
                LoginResponse.builder()
                        .id(user.getUsers_id())
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken) // 기존 리프레시 토큰 유지
                        .build()
        );
    }

    @PostMapping("/get-refresh-token")
    public ResponseEntity<String> getRefreshToken(@RequestHeader("Authorization") String kakaoAccessToken) {
        // "Bearer " 접두어가 있는 경우 제거
        kakaoAccessToken = kakaoAccessToken.replace("Bearer ", "");

        try {
            // 카카오 액세스 토큰을 통해 사용자 정보 가져오기
            SocialUserResponse socialUserResponse = userService.getUserInfoFromKakaoAccessToken(kakaoAccessToken);
            Long kakaoUserId = socialUserResponse.getKakaoUserId();

            // 추가 매개변수가 필요할 경우, 해당 값을 준비 (예: 닉네임, 추가 정보 등)
            String additionalParameter = socialUserResponse.getName(); // 예시로 닉네임 사용

            // 카카오 사용자 ID를 통해 유저 정보를 찾거나 새로 생성
            Users user = userService.findOrCreateUserByKakaoId(kakaoUserId, socialUserResponse, additionalParameter);

            // 새로운 JWT 리프레시 토큰 생성 (14일 유효)
            String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(user.getUsers_id()), new Date(System.currentTimeMillis() + 1209600000)); // 14일 유효

            // 리프레시 토큰 반환
            return ResponseEntity.ok(refreshToken);

        } catch (Exception e) {
            // 에러 처리 및 예외 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Failed to validate Kakao access token or retrieve user info.");
        }
    }

}