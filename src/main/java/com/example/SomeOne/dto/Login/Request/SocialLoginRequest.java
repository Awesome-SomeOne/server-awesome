package com.example.SomeOne.dto.Login.Request;

import com.example.SomeOne.domain.enums.UserType;
import lombok.Getter;

import jakarta.validation.constraints.NotNull;


@Getter
public class SocialLoginRequest {
    private UserType userType;
    private String code;

    public SocialLoginRequest(UserType userType, String code) {
        this.userType = userType;
        this.code = code;
    }
}