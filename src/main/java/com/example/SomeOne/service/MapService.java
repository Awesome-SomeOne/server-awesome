package com.example.SomeOne.service;

import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.TravelPlace;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.dto.Businesses.response.BusinessResponse;
import com.example.SomeOne.repository.BusinessesRepository;
import com.example.SomeOne.repository.TravelPlaceRepository;
import com.example.SomeOne.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapService {

    private final TravelPlaceRepository travelPlaceRepository;
    private final UserRepository userRepository;
    private final BusinessesRepository businessesRepository;

    // 비즈니스 정보 마커 표시
    public List<BusinessResponse> getBusinessLocations() {
        List<TravelPlace> places = travelPlaceRepository.findAll();
        return places.stream()
                .map(place -> new BusinessResponse(place.getBusinesses()))
                .collect(Collectors.toList());
    }

    // 비즈니스 이름으로 장소 검색
    public List<BusinessResponse> searchBusinesses(String keyword) {
        List<Businesses> businesses = businessesRepository.findByKeyword(keyword);
        return businesses.stream()
                .map(BusinessResponse::new)
                .collect(Collectors.toList());
    }

    // 특정 사용자의 여행 장소 가져오기
    public List<BusinessResponse> getBusinessLocationsByUser(Long userId) {
        // userId로 Users 객체를 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 특정 사용자의 여행 장소 가져오기
        List<TravelPlace> places = travelPlaceRepository.findByTravelPlans_User(user);

        // BusinessResponse 생성
        return places.stream()
                .map(place -> new BusinessResponse(place.getBusinesses()))
                .collect(Collectors.toList());
    }


    // 사용자 ID와 검색어를 기반으로 자신의 여행 장소 필터링
    public List<BusinessResponse> searchMyPlaces(Long userId, String keyword) {
        List<TravelPlace> travelPlaces = travelPlaceRepository.findByUserIdAndBusinessNameContaining(userId, keyword);

        // 여행 장소가 없을 경우
        if (travelPlaces.isEmpty()) {
            throw new EntityNotFoundException("No travel places found for user ID: " + userId + " with business name containing: " + keyword);
        }

        return travelPlaces.stream()
                .map(travelPlace -> new BusinessResponse(travelPlace.getBusinesses()))
                .collect(Collectors.toList());
    }

    @Value("${api.kakao.map.apikey}")
    private String apiKey;

    public List<BusinessResponse> findPlacesByKeyword(String query) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + query;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "KakaoAK " + apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return parseResponse(response.getBody());
    }

    private List<BusinessResponse> parseResponse(String jsonResponse) {
        List<BusinessResponse> businessResponses = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray documents = jsonObject.getJSONArray("documents");

        for (int i = 0; i < documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);
            BusinessResponse response = BusinessResponse.builder()
                    .businessName(document.getString("place_name"))
                    .address(document.optString("road_address_name", document.optString("address_name"))) // 도로명 주소 또는 일반 주소
                    .mapX(document.getString("x"))  // X 좌표
                    .mapY(document.getString("y"))  // Y 좌표
                    .imageUrl(document.optString("image_url", ""))  // 이미지 URL (API에서 제공하는 경우)
                    .build();
            businessResponses.add(response);
        }

        return businessResponses;
    }
}
