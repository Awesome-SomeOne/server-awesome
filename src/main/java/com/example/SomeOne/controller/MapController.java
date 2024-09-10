package com.example.SomeOne.controller;


import com.example.SomeOne.dto.Businesses.response.BusinessResponse;
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

    // 비즈니스 정보 마커 표시를 위한 엔드포인트
    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessResponse>> getBusinessMarkers() {
        List<BusinessResponse> businessMarkers = mapService.getBusinessLocations();
        return ResponseEntity.ok(businessMarkers);
    }

    // 특정 사용자의 여행 장소만 마커로 표시하는 엔드포인트
    @GetMapping("/businesses/user/{userId}")
    public ResponseEntity<List<BusinessResponse>> getUserBusinessMarkers(@PathVariable Long userId) {
        List<BusinessResponse> businessMarkers = mapService.getBusinessLocationsByUser(userId);
        return ResponseEntity.ok(businessMarkers);
    }

    //내 여행 장소 검색 API
    @GetMapping("/my-places/search")
    public ResponseEntity<List<BusinessResponse>> searchMyPlaces(
            @RequestParam Long userId,
            @RequestParam String keyword) {
        List<BusinessResponse> places = mapService.searchMyPlaces(userId, keyword);
        return ResponseEntity.ok(places);
    }

    // 섬 데이터 비즈니스 장소 검색
    @GetMapping("/businesses/search")
    public ResponseEntity<?> searchBusinesses(@RequestParam String keyword) {
        List<BusinessResponse> places = mapService.searchBusinesses(keyword);

        if (places.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("검색 결과가 없습니다.");
        }

        return ResponseEntity.ok(places);
    }

    // 카카오 API를 사용한 장소 검색
    @GetMapping("/search")
    public ResponseEntity<List<BusinessResponse>> searchPlaces(@RequestParam String query) {
        List<BusinessResponse> results = mapService.findPlacesByKeyword(query);
        return ResponseEntity.ok(results);
    }
}
