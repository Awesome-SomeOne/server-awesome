package com.example.SomeOne.service;

import com.example.SomeOne.domain.Users;
import com.example.SomeOne.dto.Login.Request.SocialLoginRequest;
import com.example.SomeOne.dto.Login.Response.LoginResponse;
import com.example.SomeOne.dto.Login.Response.SocialAuthResponse;
import com.example.SomeOne.dto.Login.Response.SocialUserResponse;
import com.example.SomeOne.dto.Login.Response.UserResponse;
import com.example.SomeOne.dto.Login.Request.UserJoinRequest;
import com.example.SomeOne.dto.Login.Response.UserJoinResponse;
import com.example.SomeOne.domain.enums.UserType;
import com.example.SomeOne.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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

    public LoginResponse doSocialLogin(SocialLoginRequest request) {
        // 인가 코드 로그
        log.info("Authorization code received: {}", request.getCode());

        SocialLoginService loginService = this.getLoginService(request.getUserType());

        //인가코드로 토큰 생성
        SocialAuthResponse socialAuthResponse = loginService.getAccessToken(request.getCode());
        log.info("Access token received: {}", socialAuthResponse.getAccess_token());

        //토큰으로 카카오에 유저 정보 요청
        SocialUserResponse socialUserResponse = loginService.getUserInfo(socialAuthResponse.getAccess_token());
        log.info("socialUserResponse: {}", socialUserResponse);

        //받아온 유저정보로 이미 디비에서 찾기
        Optional<Users> existingUser = userRepository.findByUserId(socialUserResponse.getId());

        //없는정보 -> 회원가입 (데이터 생성)
        if (existingUser.isEmpty()) {
            // 새로운 사용자 저장
            this.joinUser(UserJoinRequest.builder()
                    .userId(socialUserResponse.getId())
                    .userEmail(socialUserResponse.getEmail())  // email이 없는 경우 phone_number로 변경 가능
                    .userName(socialUserResponse.getName())  // username을 사용
                    .userType(request.getUserType())
                    .build());
        }

        //유저 검색
        Users user = userRepository.findByUserId(socialUserResponse.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        //검색된 유저의 아이디값을 반환
        return LoginResponse.builder()
                .id(user.getUsers_id())
                .build();
    }

    private UserJoinResponse joinUser(UserJoinRequest userJoinRequest) {
        Users user = userRepository.save(
                Users.builder()
                        .userType(userJoinRequest.getUserType())
                        .username(userJoinRequest.getUserName())  // username을 사용
                        .phone_number(userJoinRequest.getUserEmail())  // 이메일을 phone_number로 사용하거나 수정
                        .nickname(userJoinRequest.getUserName())  // nickname 설정
                        .build()
        );

        return UserJoinResponse.builder()
                .id(user.getUsers_id())
                .build();
    }

    private SocialLoginService getLoginService(UserType userType) {
        return loginServices.stream()
                .filter(service -> userType.equals(service.getServiceName()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No social login service found for " + userType));
    }

    public UserResponse getUser(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return UserResponse.builder()
                .id(user.getUsers_id())
                .userId(user.getUsername())  // username을 사용
                .userEmail(user.getPhone_number())  // phone_number를 이메일로 사용하거나 email 필드 추가
                .userName(user.getNickname())  // nickname을 사용
                .build();
    }

    public Users findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
    }

    public void logoutFromKakao(String accessToken) {
        String logoutUrl = "https://someone.com/logout";
    }

    public void unlinkKakaoUser(String accessToken) {
        String unlinkUrl = "https://kapi.kakao.com/v1/user/unlink";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(unlinkUrl, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                // 사용자 탈퇴 성공 처리
                System.out.println("사용자 탈퇴 성공");
            }
        } catch (Exception e) {
            // 오류 처리
            e.printStackTrace(); // 최소한의 오류 처리
        }
    }


}