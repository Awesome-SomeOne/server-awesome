package com.example.SomeOne.controller;


import com.example.SomeOne.domain.enums.UserType;
import com.example.SomeOne.dto.Login.Request.SocialLoginRequest;
import com.example.SomeOne.dto.Login.Response.LoginResponse;
import com.example.SomeOne.dto.Login.Response.UserResponse;
import com.example.SomeOne.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/social-login")
    public ResponseEntity<LoginResponse> doSocialLogin(@RequestBody @Valid SocialLoginRequest request) {

        return ResponseEntity.created(URI.create("/social-login"))
                .body(userService.doSocialLogin(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                userService.getUser(id)
        );
    }


    @GetMapping("/kakao/callback")
    public ResponseEntity<LoginResponse> kakaoCallback(@RequestParam(value="code") String code) {
        // 받은 인가 코드로 로그인 처리 로직
        SocialLoginRequest request = new SocialLoginRequest(UserType.KAKAO, code);
        return ResponseEntity.ok(userService.doSocialLogin(request));
    }

}