package com.example.SomeOne.service;

import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.TravelPlace;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.dto.Businesses.response.BusinessResponse;
import com.example.SomeOne.repository.BusinessesRepository;
import com.example.SomeOne.repository.FavoritesRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final FavoritesRepository favoritesRepository;

    // 비즈니스 정보 마커 표시
    public List<BusinessResponse> getBusinessLocations(Long userId) {
        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 모든 여행 장소 가져오기
        List<TravelPlace> places = travelPlaceRepository.findAll();

        // 각 비즈니스에 대해 즐겨찾기 여부 확인 및 BusinessResponse 생성
        return places.stream()
                .map(place -> {
                    Businesses business = place.getBusinesses();
                    boolean isFavorite = favoritesRepository.findByUserAndBusiness(user, business).isPresent();
                    return new BusinessResponse(business, isFavorite);
                })
                .collect(Collectors.toList());
    }

    // 비즈니스 이름으로 장소 검색
    public List<BusinessResponse> searchBusinesses(String keyword) {
        // SecurityContextHolder를 통해 인증된 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        // 사용자 ID 가져오기
        Long userId = Long.parseLong(authentication.getName());

        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 키워드로 비즈니스 검색
        List<Businesses> businesses = businessesRepository.findByKeyword(keyword);

        // 검색 결과가 없을 경우 빈 리스트 반환
        if (businesses.isEmpty()) {
            return new ArrayList<>();  // 빈 리스트 반환
        }

        // 각 비즈니스에 대해 즐겨찾기 여부 확인 로직 제거 및 기본값(false) 설정
        return businesses.stream()
                .map(business -> new BusinessResponse(business, false))  // 즐겨찾기 여부는 기본값(false)로 설정
                .collect(Collectors.toList());
    }

    // 특정 사용자의 여행 장소 가져오기
    public List<BusinessResponse> getBusinessLocationsByUser(Long userId) {
        // userId로 Users 객체를 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 특정 사용자의 여행 장소 가져오기
        List<TravelPlace> places = travelPlaceRepository.findByTravelPlans_User(user);

        // 각 비즈니스에 대해 즐겨찾기 여부 확인 및 BusinessResponse 생성
        return places.stream()
                .map(place -> {
                    Businesses business = place.getBusinesses();
                    boolean isFavorite = favoritesRepository.findByUserAndBusiness(user, business).isPresent();
                    return new BusinessResponse(business, isFavorite);
                })
                .collect(Collectors.toList());
    }

    // 사용자 ID와 검색어를 기반으로 자신의 여행 장소 필터링
    public List<BusinessResponse> searchMyPlaces(Long userId, String keyword) {
        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 키워드로 자신의 여행 장소 필터링
        List<TravelPlace> travelPlaces = travelPlaceRepository.findByUserIdAndBusinessNameContaining(userId, keyword);

        // 여행 장소가 없을 경우 예외 처리
        if (travelPlaces.isEmpty()) {
            throw new EntityNotFoundException("No travel places found for user ID: " + userId + " with business name containing: " + keyword);
        }

        // 각 비즈니스에 대해 즐겨찾기 여부 확인 및 BusinessResponse 생성
        return travelPlaces.stream()
                .map(travelPlace -> {
                    Businesses business = travelPlace.getBusinesses();
                    boolean isFavorite = favoritesRepository.findByUserAndBusiness(user, business).isPresent();
                    return new BusinessResponse(business, isFavorite);
                })
                .collect(Collectors.toList());
    }

}
