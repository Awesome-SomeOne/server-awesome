package com.example.SomeOne.controller;


import com.example.SomeOne.config.SecurityUtil;
import com.example.SomeOne.dto.Businesses.response.BusinessResponse;
import com.example.SomeOne.service.KakaoMapService;
import com.example.SomeOne.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;
    private final KakaoMapService kakaoMapService;


    // 비즈니스 정보 마커 표시를 위한 엔드포인트 (모든 사용자에 대해)
    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessResponse>> getBusinessMarkers(@RequestParam Long userId) {
        // 모든 비즈니스 장소에 대해 사용자 즐겨찾기 여부 포함
        List<BusinessResponse> businessMarkers = mapService.getBusinessLocations(userId);
        return ResponseEntity.ok(businessMarkers);
    }

    // 특정 사용자의 여행 장소만 마커로 표시하는 엔드포인트 (JWT에서 사용자 ID 가져오기)
    @GetMapping("/businesses/user")
    public ResponseEntity<List<BusinessResponse>> getUserBusinessMarkers() {
        Long userId = SecurityUtil.getAuthenticatedUserId(); // JWT에서 사용자 ID 가져오기
        List<BusinessResponse> businessMarkers = mapService.getBusinessLocationsByUser(userId);
        return ResponseEntity.ok(businessMarkers);
    }

    // 내 여행 장소 검색 API (JWT에서 사용자 ID 가져오기)
    @GetMapping("/my-places/search")
    public ResponseEntity<List<BusinessResponse>> searchMyPlaces(
            @RequestParam String keyword) {
        Long userId = SecurityUtil.getAuthenticatedUserId(); // JWT에서 사용자 ID 가져오기
        List<BusinessResponse> places = mapService.searchMyPlaces(userId, keyword);
        return ResponseEntity.ok(places);
    }

    // 섬 데이터 비즈니스 장소 검색
    @GetMapping("/businesses/search")
    public ResponseEntity<?> searchBusinesses(@RequestParam String keyword) {
        List<BusinessResponse> places = mapService.searchBusinesses(keyword); // userId 전달하지 않음

        return ResponseEntity.ok(places);
    }

    // 카카오 API를 사용한 장소 검색
    @GetMapping("/kakao/search")
    public ResponseEntity<List<BusinessResponse>> searchPlaces(@RequestParam String query) {
        List<BusinessResponse> results = kakaoMapService.findPlacesByKeyword(query);
        return ResponseEntity.ok(results);
    }
}

