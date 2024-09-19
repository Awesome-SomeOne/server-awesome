package com.example.SomeOne.controller;


import com.example.SomeOne.config.auth.JwtTokenProvider;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.domain.enums.UserType;
import com.example.SomeOne.dto.Login.Request.SocialLoginRequest;
import com.example.SomeOne.dto.Login.Response.LoginResponse;
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

        return ResponseEntity.created(URI.create("/social-login"))
                .body(userService.doSocialLogin(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                userService.getUser(id)
        );
    }


    @GetMapping("/kakao/callback")
    public ResponseEntity<LoginResponse> kakaoCallback(@RequestParam(value = "code") String code) {
        // 받은 인가 코드로 로그인 처리 로직
        SocialLoginRequest request = new SocialLoginRequest(UserType.KAKAO, code);
        return ResponseEntity.ok(userService.doSocialLogin(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken) {
        userService.logoutFromKakao(accessToken);
        return ResponseEntity.ok("Logged out successfully.");
    }

    @PostMapping("/kakao/unlink")
    public ResponseEntity<String> unlinkKakaoUser(@RequestHeader("Authorization") String accessToken) {
        userService.unlinkKakaoUser(accessToken);
        return ResponseEntity.ok("탈퇴 완료");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
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


}