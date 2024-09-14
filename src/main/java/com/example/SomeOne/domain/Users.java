package com.example.SomeOne.domain;

import com.example.SomeOne.domain.enums.UserType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue
    private Long users_id; //키값

    private String username;  // 로그인 시 사용하는 이름
    private String password;
    private String phone_number;  // 이메일 용도로 사용 가능
    private String nickname;  // 별명
    private String profile_image;
    private Boolean social_login;
    private String userId; // 닉네임

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "island_id")
    private Island island;

    @Enumerated(EnumType.STRING)  // 열거형을 문자열로 저장
    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'NORMAL'")  // 기본값을 NORMAL로 설정
    private UserType userType;
}
