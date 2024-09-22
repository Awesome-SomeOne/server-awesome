package com.example.SomeOne.dto.Login.Response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SocialUserResponse {
    private String id;
    private String email;
    private String name;
    private String gender;
    private String birthday;
    private Long kakaoUserId;

    // 생성자 추가
    public SocialUserResponse(Long kakaoUserId, String email, String name) {
        this.kakaoUserId = kakaoUserId;
        this.email = email;
        this.name = name;
    }

}