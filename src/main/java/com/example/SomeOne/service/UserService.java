package com.example.SomeOne.service;

import com.example.SomeOne.domain.Users;
import com.example.SomeOne.config.auth.JwtTokenProvider;
import com.example.SomeOne.dto.Login.Request.SocialLoginRequest;
import com.example.SomeOne.dto.Login.Response.LoginResponse;
import com.example.SomeOne.dto.Login.Response.SocialAuthResponse;
import com.example.SomeOne.dto.Login.Response.SocialUserResponse;
import com.example.SomeOne.dto.Login.Response.UserResponse;
import com.example.SomeOne.dto.Login.Request.UserJoinRequest;
import com.example.SomeOne.dto.Login.Response.UserJoinResponse;
import com.example.SomeOne.domain.enums.UserType;
import com.example.SomeOne.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final List<SocialLoginService> loginServices;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse doSocialLogin(SocialLoginRequest request) {
        SocialLoginService loginService = this.getLoginService(request.getUserType());

        // Access Token과 사용자 정보 가져오기
        SocialAuthResponse socialAuthResponse = loginService.getAccessToken(request.getCode());
        SocialUserResponse socialUserResponse = loginService.getUserInfo(socialAuthResponse.getAccess_token());

        // 사용자 조회 및 없는 경우 새 사용자 등록
        Users user = userRepository.findByUserId(socialUserResponse.getId())
                .orElseGet(() -> this.joinUser(UserJoinRequest.builder()
                        .userId(socialUserResponse.getId())
                        .userEmail(socialUserResponse.getEmail())
                        .userName(socialUserResponse.getName())
                        .userType(request.getUserType())
                        .build()));

        // JWT 생성 (Access Token과 Refresh Token 생성)
        String accessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getUsers_id()), new Date(System.currentTimeMillis() + 3600000)); // 1시간 유효
        String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(user.getUsers_id()), new Date(System.currentTimeMillis() + 1209600000)); // 14일 유효

        // 사용자 객체에 리프레시 토큰 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 로그인 응답 생성
        return LoginResponse.builder()
                .id(user.getUsers_id())
                .accessToken(accessToken)
                .refreshToken(refreshToken)  // 리프레시 토큰 포함
                .build();
    }

    // 새로운 사용자 등록 로직
    private Users joinUser(UserJoinRequest userJoinRequest) {
        Users user = Users.builder()
                .userType(userJoinRequest.getUserType())
                .username(userJoinRequest.getUserName())  // username 설정
                .phone_number(userJoinRequest.getUserEmail())  // 이메일 설정 (phone_number로 대체한 경우)
                .nickname(userJoinRequest.getUserName())  // nickname 설정
                .build();

        return userRepository.save(user);  // 저장 후 반환
    }

    // 소셜 로그인 서비스 가져오기
    private SocialLoginService getLoginService(UserType userType) {
        return loginServices.stream()
                .filter(service -> userType.equals(service.getServiceName()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No social login service found for " + userType));
    }

    // 사용자 정보 조회
    public UserResponse getUser(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return UserResponse.builder()
                .id(user.getUsers_id())
                .userId(user.getUsername())  // username 설정
                .userEmail(user.getPhone_number())  // 이메일 설정
                .userName(user.getNickname())  // nickname 사용
                .build();
    }

    // 특정 사용자 조회
    public Users findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
    }

    // 카카오 로그아웃 처리
    public void logoutFromKakao(String accessToken) {
        String logoutUrl = "https://someone.com/logout";
    }

    public Long findUserIdByAccessToken(String accessToken) {
        // 1. Access Token을 사용해 카카오 API로 사용자 정보 요청
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 2. 카카오 API로 사용자 정보 조회
            ResponseEntity<String> response = restTemplate.postForEntity(userInfoUrl, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                // 3. 사용자 정보에서 고유 ID 추출 (JSON Parsing 필요)
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                Long kakaoUserId = root.path("id").asLong();

                // 4. 로컬 데이터베이스에서 해당 카카오 사용자 ID로 사용자 조회
                Users user = userRepository.findByUserId(String.valueOf(kakaoUserId))
                        .orElseThrow(() -> new NoSuchElementException("해당 사용자를 찾을 수 없습니다."));

                return user.getUsers_id();  // 로컬 데이터베이스의 사용자 ID 반환
            }
        } catch (Exception e) {
            log.error("Access Token을 사용한 사용자 정보 조회 실패", e);
            throw new RuntimeException("사용자 조회 실패");
        }

        throw new RuntimeException("Access Token을 통해 사용자 정보를 가져올 수 없습니다.");
    }


    // 카카오 사용자 탈퇴 처리
    public void unlinkKakaoUser(String accessToken) {
        String unlinkUrl = "https://kapi.kakao.com/v1/user/unlink";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(unlinkUrl, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("카카오 사용자 탈퇴 성공");

                // 로컬 데이터베이스에서 사용자 삭제 (user_id 기준으로 삭제)
                Long userId = findUserIdByAccessToken(accessToken); // 사용자 ID를 Access Token으로 찾는 로직 필요
                userRepository.deleteById(userId); // 해당 사용자 ID로 삭제
            }
        } catch (Exception e) {
            log.error("카카오 사용자 탈퇴 실패", e);
        }
    }

}


