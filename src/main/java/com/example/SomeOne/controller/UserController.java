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
            @RequestParam("nickname") String nickname) { // 닉네임 파라미터 추가

        // "Bearer " 접두어 제거
        accessToken = accessToken.replace("Bearer ", "");

        // 닉네임을 이용하여 JWT 발급
        LoginResponse loginResponse = userService.issueJwtToken(accessToken, nickname);
        return ResponseEntity.ok(loginResponse);
    }

    // 엑세스 토큰으로 JWT 발급
    @PostMapping("/issue-jwt-with-access-token")
    public ResponseEntity<LoginResponse> issueJwtWithAccessToken(
            @RequestHeader("Authorization") String accessToken) {
        // "Bearer " 접두어 제거
        accessToken = accessToken.replace("Bearer ", "").trim();

        // 엑세스 토큰을 이용하여 JWT 발급
        LoginResponse loginResponse = userService.issueJwtToken(accessToken);
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken) {
        // "Bearer " 접두어 제거
        accessToken = accessToken.replace("Bearer ", "");

        // 카카오 로그아웃 서비스 호출
        userService.logoutFromKakao(accessToken);

        // 성공 응답 반환
        return ResponseEntity.ok("Logged out from Kakao successfully.");
    }

    // 회원탈퇴
    @PostMapping("/unlink")
    public ResponseEntity<String> unlinkKakaoUser(@RequestHeader("Authorization") String accessToken) {
        accessToken = accessToken.replace("Bearer ", "");

        // 카카오 회원 탈퇴 서비스 호출
        userService.unlinkKakaoUser(accessToken);

        // 성공 응답 반환
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
            String additionalParameter = socialUserResponse.getNickname(); // 예시로 닉네임 사용

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

    // 엑세스 토큰과 닉네임으로 유저 닉네임 반환하기
    @GetMapping("/nickname")
    public ResponseEntity<String> getNicknameByJwt(@RequestHeader("Authorization") String jwtAccessToken) {
        // JWT 토큰에서 사용자 ID 추출
        jwtAccessToken = jwtAccessToken.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromToken(jwtAccessToken);

        // 사용자 ID로 사용자 정보 조회
        Users user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        // 사용자 닉네임 반환
        String nickname = user.getNickname();
        return ResponseEntity.ok(nickname != null ? nickname : "닉네임이 없습니다");
    }

}