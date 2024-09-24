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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long users_id;

    private String kakaoUserId;

    private String username;  // 로그인 시 사용하는 이름
    private String password;
    private String phone_number;  // 이메일 용도로 사용 가능
    private String nickname;  // 별명
    private String profile_image;
    private Boolean social_login;
    private Long userId;
    private String refreshToken;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "island_id")
    private Island island;

    @Enumerated(EnumType.STRING)  // 열거형을 문자열로 저장
    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'NORMAL'")  // 기본값을 NORMAL로 설정
    private UserType userType;

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // 리프레시 토큰 만료 여부 등을 확인할 수 있는 추가 메서드
    public boolean isRefreshTokenValid(String token) {
        // 토큰 유효성 체크 로직 추가 가능 (예: 만료 시간 확인)
        return this.refreshToken.equals(token);
    }
}
