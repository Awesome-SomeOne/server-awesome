package com.example.SomeOne.controller;


import com.example.SomeOne.config.auth.JwtTokenProvider;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.domain.enums.UserType;
import com.example.SomeOne.dto.Login.Request.SocialLoginRequest;
import com.example.SomeOne.dto.Login.Response.LoginResponse;
import com.example.SomeOne.dto.Login.Response.SocialUserResponse;
import com.example.SomeOne.dto.Login.Response.TokenResponse;
import com.example.SomeOne.dto.Login.Response.UserResponse;
import com.example.SomeOne.repository.UserRepository;
import com.example.SomeOne.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.Date;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/social-login")
    public ResponseEntity<LoginResponse> doSocialLogin(@RequestBody @Valid SocialLoginRequest request) {
        return ResponseEntity.ok(userService.doSocialLogin(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<LoginResponse> kakaoCallback(@RequestParam(value = "code") String code) {
        // 카카오 소셜 로그인 콜백 처리 로직
        SocialLoginRequest request = new SocialLoginRequest(UserType.KAKAO, code);
        return ResponseEntity.ok(userService.doSocialLogin(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken) {
        userService.logoutFromKakao(accessToken);
        return ResponseEntity.ok("Logged out successfully.");
    }

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

            // 카카오 사용자 ID를 통해 유저 정보를 찾거나 새로 생성
            Users user = userService.findOrCreateUserByKakaoId(kakaoUserId, socialUserResponse);

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