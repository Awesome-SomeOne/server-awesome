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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        SocialLoginService loginService = this.getLoginService(request.getUserType());

        SocialAuthResponse socialAuthResponse = loginService.getAccessToken(request.getCode());

        SocialUserResponse socialUserResponse = loginService.getUserInfo(socialAuthResponse.getAccess_token());
        log.info("socialUserResponse: {}", socialUserResponse);

        Optional<Users> existingUser = userRepository.findByUserId(socialUserResponse.getId());

        if (existingUser.isEmpty()) {
            // 새로운 사용자 저장
            this.joinUser(UserJoinRequest.builder()
                    .userId(socialUserResponse.getId())
                    .userEmail(socialUserResponse.getEmail())  // email이 없는 경우 phone_number로 변경 가능
                    .userName(socialUserResponse.getName())  // username을 사용
                    .userType(request.getUserType())
                    .build());
        }

        Users user = userRepository.findByUserId(socialUserResponse.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

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
}




