package com.example.SomeOne.service;



import com.example.SomeOne.domain.enums.UserType;
import com.example.SomeOne.dto.Login.Response.SocialAuthResponse;
import com.example.SomeOne.dto.Login.Response.SocialUserResponse;
import org.springframework.stereotype.Service;

@Service
public interface SocialLoginService {
    UserType getServiceName();
    SocialAuthResponse getAccessToken(String authorizationCode);
    SocialUserResponse getUserInfo(String accessToken);
}