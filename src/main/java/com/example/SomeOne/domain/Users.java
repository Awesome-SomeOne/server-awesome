package com.example.SomeOne.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Users {

    @Id @GeneratedValue
    private Long users_id;

    private String username;
    private String password;
    private String phone_number;
    private String nickname;
    private String profile_image;
    private Boolean social_login;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "island_id")
    private Island island;
}
