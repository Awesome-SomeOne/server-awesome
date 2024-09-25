package com.example.SomeOne.feign.kakao;

import com.example.SomeOne.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "kakaoAuth", url="https://kauth.kakao.com", configuration = {FeignConfiguration.class})
public interface KakaoAuthApi {
    @PostMapping("/oauth/token")
    ResponseEntity<String> getAccessToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("grant_type") String grantType,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String authorizationCode
    );
}