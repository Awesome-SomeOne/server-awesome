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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final List<SocialLoginService> loginServices;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, String> cachedAccessTokens = new ConcurrentHashMap<>(); // 인가 코드로 액세스 토큰 캐싱

    // 인가 코드로 카카오 액세스 토큰 발급 및 사용자 정보 저장
    public synchronized String getAccessTokenFromKakao(String code) {
        // 캐싱된 액세스 토큰이 있는지 확인
        if (cachedAccessTokens.containsKey(code)) {
            log.info("Returning cached access token for code: {}", code);
            return cachedAccessTokens.get(code);  // 이미 캐싱된 액세스 토큰 반환
        }

        SocialLoginService loginService = this.getLoginService(UserType.KAKAO);

        try {
            // 인가 코드를 사용하여 액세스 토큰 발급 (한 번만 사용)
            SocialAuthResponse socialAuthResponse = loginService.getAccessToken(code);

            // 액세스 토큰 캐싱
            String accessToken = socialAuthResponse.getAccess_token();
            cachedAccessTokens.put(code, accessToken);
            log.info("Cached access token for code: {}", code);

            // 사용자 정보 가져오기
            SocialUserResponse userInfo = getUserInfoFromKakaoAccessToken(accessToken);

            // 사용자 정보가 DB에 없으면 저장
            findOrCreateUserByKakaoId(userInfo.getKakaoUserId(), userInfo, userInfo.getNickname());

            return accessToken;
        } catch (HttpClientErrorException e) {
            log.error("Error getting access token from Kakao", e);
            throw new RuntimeException("Failed to get access token from Kakao", e);
        }
    }

    // 액세스 토큰을 사용하여 JWT 발급
    public LoginResponse issueJwtToken(String accessToken, String nickname) {
        // 액세스 토큰으로 사용자 정보 가져오기
        SocialUserResponse socialUserResponse = getUserInfoFromKakaoAccessToken(accessToken);

        // 사용자 조회 또는 생성 (닉네임을 사용하여 사용자 정보 생성)
        Users user = findOrCreateUserByKakaoId(socialUserResponse.getKakaoUserId(), socialUserResponse, nickname);

        // JWT 생성
        String jwtAccessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getUsers_id()), new Date(System.currentTimeMillis() + 28800000)); // 8시간 유효

        String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(user.getUsers_id()), new Date(System.currentTimeMillis() + 1209600000)); // 14일 유효

        // 사용자 객체에 리프레시 토큰 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return LoginResponse.builder()
                .id(user.getUsers_id())
                .accessToken(jwtAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 새로운 사용자 등록 로직
    private Users joinUser(UserJoinRequest userJoinRequest) {
        Users user = Users.builder()
                .userType(userJoinRequest.getUserType())
                .kakaoUserId(userJoinRequest.getKakaoUserId()) // kakaoUserId 설정
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

    // 로그아웃 처리 (카카오 로그아웃 호출)
    public void logoutFromKakao(String accessToken) {
        String kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/logout"; // 카카오 로그아웃 API 엔드포인트
        HttpHeaders headers = new HttpHeaders();

        // Bearer Authorization 설정
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 카카오 API에 로그아웃 요청을 보냄
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoLogoutUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Kakao user successfully logged out");
            } else {
                log.error("Kakao logout failed, response code: {}, response body: {}", response.getStatusCode(), response.getBody());
            }
        } catch (HttpClientErrorException e) {
            log.error("Kakao logout failed, error: {}, response body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to logout from Kakao", e);
        }
    }

    // 카카오 사용자 탈퇴 처리
    public void unlinkKakaoUser(String accessToken) {
        String kakaoUnlinkUrl = "https://kapi.kakao.com/v1/user/unlink"; // 카카오 회원 탈퇴 API 엔드포인트
        HttpHeaders headers = new HttpHeaders();

        // Bearer Authorization 설정
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 카카오 API에 회원 탈퇴 요청을 보냄
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoUnlinkUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Kakao account successfully unlinked");
            } else {
                log.error("Kakao unlink failed, response code: {}, response body: {}", response.getStatusCode(), response.getBody());
            }
        } catch (HttpClientErrorException e) {
            log.error("Kakao unlink failed, error: {}, response body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to unlink Kakao account", e);
        }
    }

    // 엑세스 토큰에서 사용자 가져오기
    public Long findUserIdByAccessToken(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.trim());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Kakao API 호출
            ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());

                // 사용자 ID 가져오기
                Long kakaoUserId = root.path("id").asLong();
                log.info("Successfully retrieved Kakao user ID: {}", kakaoUserId);

                // 사용자 정보를 로컬 DB에서 조회 (findByKakaoUserId 메서드를 사용)
                return userRepository.findByKakaoUserId(String.valueOf(kakaoUserId))  // 이 부분이 중요한 수정입니다.
                        .map(Users::getUsers_id)
                        .orElseThrow(() -> new NoSuchElementException("User not found for Kakao ID: " + kakaoUserId));

            } else {
                log.error("Kakao API error, status code: {}, response body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Kakao API call failed: Non-2xx status code");
            }
        } catch (HttpClientErrorException e) {
            // 카카오 API 호출 오류 처리
            log.error("Failed to retrieve user info with access token - HTTP error: {}, response body: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Kakao API call failed: " + e.getMessage(), e);
        } catch (JsonProcessingException e) {
            // JSON 처리 오류 처리
            log.error("Failed to process the response body as JSON", e);
            throw new RuntimeException("Failed to process the response body as JSON", e);
        } catch (Exception e) {
            // 그 외의 예외 처리
            log.error("Unexpected error occurred while retrieving user info", e);
            throw new RuntimeException("Failed to retrieve user info", e);
        }
    }

    // 리프레시 토큰 갱신 로직
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }

        // 리프레시 토큰에서 사용자 ID 추출
        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 사용자 정보 조회
        Users user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getUsers_id()), new Date(System.currentTimeMillis() + 3600000)); // 1시간 유효

        // 응답 반환
        return LoginResponse.builder()
                .id(user.getUsers_id())
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)  // 기존 리프레시 토큰 유지
                .build();
    }

    // 리프레시 토큰 반환
    public String getRefreshToken(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            log.error("Invalid access token: {}", accessToken);
            throw new IllegalArgumentException("Invalid access token");
        }

        try {
            // 액세스 토큰에서 사용자 ID 추출
            Long userId = findUserIdByAccessToken(accessToken);

            // 사용자 정보 조회
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

            // 리프레시 토큰 반환
            return user.getRefreshToken();
        } catch (Exception e) {
            log.error("Failed to retrieve refresh token for user with access token: {}", accessToken, e);
            throw new RuntimeException("Failed to retrieve refresh token", e);
        }
    }

    // 카카오 사용자 ID로 유저를 찾거나 새로 생성하는 메서드
    public Users findOrCreateUserByKakaoId(Long kakaoUserId, SocialUserResponse socialUserResponse, String additionalParameter) {
        Optional<Users> existingUser = userRepository.findByKakaoUserId(String.valueOf(kakaoUserId));
        if (existingUser.isPresent()) {
            log.info("User found for Kakao ID: {}", kakaoUserId);
            return existingUser.get();
        }

        // 사용자가 없으면 새 유저 생성
        log.info("No user found for Kakao ID: {}, creating a new user", kakaoUserId);
        Users newUser = Users.builder()
                .kakaoUserId(String.valueOf(kakaoUserId)) // 카카오 사용자 ID 저장
                .username(socialUserResponse.getNickname()) // nickname을 username으로 설정
                .userType(UserType.KAKAO) // 사용자 타입을 KAKAO로 설정
                .build();

        return userRepository.save(newUser); // 새로운 유저 저장 및 반환
    }

    public SocialUserResponse getUserInfoFromKakaoAccessToken(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());

                // 카카오 사용자 정보를 JSON에서 추출
                Long kakaoUserId = root.path("id").asLong();
                String email = root.path("kakao_account").path("email").asText();
                String name = root.path("properties").path("nickname").asText();

                return new SocialUserResponse(kakaoUserId, email, name);
            } else {
                log.error("Kakao API error: {}", response.getStatusCode());
                throw new RuntimeException("Failed to retrieve user info from Kakao API");
            }
        } catch (Exception e) {
            log.error("Failed to retrieve user info from Kakao API", e);
            throw new RuntimeException("Failed to retrieve user info from Kakao API", e);
        }
    }
}


