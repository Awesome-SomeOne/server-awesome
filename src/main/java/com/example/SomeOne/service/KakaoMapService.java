package com.example.SomeOne.service;

import com.example.SomeOne.dto.Businesses.response.BusinessResponse;
import com.example.SomeOne.dto.Businesses.response.KakaoPlaceSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KakaoMapService {

    private final RestTemplate restTemplate;

    // 카카오 API 키 (application.yml 또는 application.properties에 설정)
    @Value("${api.kakao.map.apiKey}")
    private String kakaoApiKey;

    private static final String KAKAO_PLACE_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    // 카카오 API로 장소 검색
    public List<BusinessResponse> findPlacesByKeyword(String query) {
        String url = KAKAO_PLACE_SEARCH_URL + "?query=" + query;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);  // REST API 키를 Authorization 헤더에 추가

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // API 호출
        ResponseEntity<KakaoPlaceSearchResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, KakaoPlaceSearchResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // 검색 결과를 변환하여 반환
            return response.getBody().toBusinessResponseList(); // 변환 로직 필요
        } else {
            throw new RuntimeException("Failed to search places from Kakao API");
        }
    }
}
