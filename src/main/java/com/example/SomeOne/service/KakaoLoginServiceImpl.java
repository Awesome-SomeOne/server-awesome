package com.example.SomeOne.service;

import com.example.SomeOne.config.auth.JwtTokenProvider;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.domain.enums.UserType;
import com.example.SomeOne.dto.Login.Response.KaKaoLoginResponse;
import com.example.SomeOne.dto.Login.Response.SocialAuthResponse;
import com.example.SomeOne.dto.Login.Response.SocialUserResponse;
import com.example.SomeOne.feign.kakao.KakaoAuthApi;
import com.example.SomeOne.feign.kakao.KakaoUserApi;
import com.example.SomeOne.repository.UserRepository;
import com.example.SomeOne.utils.GsonLocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("kakaoLogin")
public class KakaoLoginServiceImpl implements SocialLoginService {
    private final KakaoAuthApi kakaoAuthApi;
    private final KakaoUserApi kakaoUserApi;

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${social.client.kakao.rest-api-key}")
    private String kakaoAppKey;
    @Value("${social.client.kakao.secret-key}")
    private String kakaoAppSecret;
    @Value("${social.client.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${social.client.kakao.grant_type}")
    private String kakaoGrantType;

    // 인가 코드가 이미 사용되었는지 체크하는 변수
    private Map<String, Boolean> usedAuthorizationCodes = new HashMap<>();

    @Override
    public UserType getServiceName() {
        return UserType.KAKAO;
    }

    @Override
    public synchronized SocialAuthResponse getAccessToken(String authorizationCode) {
        if (usedAuthorizationCodes.containsKey(authorizationCode)) {
            log.error("Authorization code {} has already been used", authorizationCode);
            throw new IllegalStateException("This authorization code has already been used.");
        }

        usedAuthorizationCodes.put(authorizationCode, true);

        ResponseEntity<String> response = kakaoAuthApi.getAccessToken(
                kakaoAppKey,
                kakaoAppSecret,
                kakaoGrantType,
                kakaoRedirectUri,
                authorizationCode
        );

        log.info("Kakao auth response: {}", response.toString());

        SocialAuthResponse socialAuthResponse = new Gson().fromJson(response.getBody(), SocialAuthResponse.class);

        if (socialAuthResponse == null || socialAuthResponse.getAccess_token() == null) {
            usedAuthorizationCodes.remove(authorizationCode);
            log.error("Failed to obtain access token for authorization code {}", authorizationCode);
            throw new IllegalStateException("Failed to obtain access token");
        }

        return socialAuthResponse;
    }

    @Override
    public SocialUserResponse getUserInfo(String accessToken) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("authorization", "Bearer " + accessToken);

        // 카카오 사용자 정보 요청
        ResponseEntity<?> response = kakaoUserApi.getUserInfo(headerMap);

        log.info("kakao user response");
        log.info(response.toString());

        String jsonString = response.getBody().toString();

        // Gson을 사용하여 JSON 응답을 파싱
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
                .create();

        KaKaoLoginResponse kaKaoLoginResponse = gson.fromJson(jsonString, KaKaoLoginResponse.class);
        KaKaoLoginResponse.KakaoLoginData kakaoLoginData = Optional.ofNullable(kaKaoLoginResponse.getKakao_account())
                .orElse(KaKaoLoginResponse.KakaoLoginData.builder().build());

        String name = Optional.ofNullable(kakaoLoginData.getProfile())
                .orElse(KaKaoLoginResponse.KakaoLoginData.KakaoProfile.builder().build())
                .getNickname();

        return SocialUserResponse.builder()
                .id(kaKaoLoginResponse.getId())
                .gender(kakaoLoginData.getGender())
                .name(name)
                .email(kakaoLoginData.getEmail())
                .build();
    }

    public String issueJwtToken(String accessToken, String nickname) {
        SocialUserResponse socialUserResponse = getUserInfo(accessToken);

        Users user = userRepository.findByKakaoUserId(String.valueOf(socialUserResponse.getKakaoUserId()))
                .orElseGet(() -> userRepository.save(Users.builder()
                        .kakaoUserId(String.valueOf(socialUserResponse.getKakaoUserId()))
                        .username(socialUserResponse.getName() != null ? socialUserResponse.getName() : "kakao_user_" + socialUserResponse.getKakaoUserId())
                        .userType(UserType.KAKAO)
                        .build()));

        String jwtAccessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getUsers_id()), new Date(System.currentTimeMillis() + 3600000)); // 1시간 유효
        String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(user.getUsers_id()), new Date(System.currentTimeMillis() + 1209600000)); // 14일 유효

        user.setRefreshToken(refreshToken);
        userRepository.save(user);


        return jwtAccessToken;
    }
}