package com.example.SomeOne.controller;

import com.example.SomeOne.domain.enums.Business_category;
import com.example.SomeOne.dto.Businesses.response.GetLandmarkResponse;
import com.example.SomeOne.dto.Businesses.response.PopularityPlaceResponse;
import com.example.SomeOne.dto.TravelPlans.request.LikeRequest;
import com.example.SomeOne.dto.TravelPlans.response.LikeResponse;
import com.example.SomeOne.service.FavoritesService;
import com.example.SomeOne.service.PopularityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/popularity")
public class PopularityController {

    private final PopularityService popularityService;
    private final FavoritesService favoritesService;

    @GetMapping("/landmark/list") // 인기관광지 둘러보기
    public ResponseEntity<List<PopularityPlaceResponse>> landmarkList(@RequestParam("userId") Long userId,
                                                                      @RequestParam("islandId") Long islandId) {
        List<PopularityPlaceResponse> response = popularityService.listLandmark(userId, islandId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommend/place") // 추천장소 리스트
    public ResponseEntity<List<PopularityPlaceResponse>> recommendPlaceList(@RequestParam("userId") Long userId,
                                                                            @RequestParam("islandId") Long islandId,
                                                                            @RequestParam("category") Business_category category) {
        List<PopularityPlaceResponse> response = popularityService.recommendPlaceList(userId, islandId, category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/landmark")
    public ResponseEntity<GetLandmarkResponse> getLandmark(@RequestParam("businessId") Long businessId) {
        GetLandmarkResponse response = popularityService.getPlace(businessId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/like")
    public ResponseEntity<LikeResponse> updateLike(@RequestBody LikeRequest request) {
        LikeResponse response = favoritesService.updateLike(request.getUserId(), request.getBusinessId());
        return ResponseEntity.ok(response);
    }
}