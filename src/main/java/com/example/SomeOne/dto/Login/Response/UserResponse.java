package com.example.SomeOne.dto.Login.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResponse {
    private Long id;
    private String userId;
    private String userName;
    private String userEmail;
    private Long kakaoUserId;
}