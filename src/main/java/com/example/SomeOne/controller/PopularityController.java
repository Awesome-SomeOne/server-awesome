package com.example.SomeOne.controller;

import com.example.SomeOne.domain.enums.Business_category;
import com.example.SomeOne.dto.Businesses.response.GetLandmarkResponse;
import com.example.SomeOne.dto.Businesses.response.PopularityPlaceResponse;
import com.example.SomeOne.service.FavoritesService;
import com.example.SomeOne.service.PopularityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/popularity")
public class PopularityController {

    private final PopularityService popularityService;
    private final FavoritesService favoritesService;

    @GetMapping("/landmark/list") // 인기관광지 둘러보기
    public List<PopularityPlaceResponse> landmarkList(@RequestParam("userId") Long userId,
                                                      @RequestParam("islandId") Long islandId) {
        return popularityService.listLandmark(userId, islandId);
    }

    @GetMapping("/recommend/place") // 추천장소 리스트
    public List<PopularityPlaceResponse> recommendPlaceList(@RequestParam("userId") Long userId,
                                                            @RequestParam("islandId") Long islandId,
                                                            @RequestParam("category") Business_category category) {
        return popularityService.recommendPlaceList(userId, islandId, category);
    }

    @GetMapping("/landmark")
    public GetLandmarkResponse getLandmark(@RequestParam("businessId") Long businessId) {
        return popularityService.getPlace(businessId);
    }

    @PatchMapping("/like")
    public void updateLike(@RequestParam("userId") Long userId,
                           @RequestParam("businessId") Long businessId) {
        boolean status = favoritesService.findFavorite(userId, businessId);
        if (status) {
            favoritesService.removeFavorite(userId, businessId);
        }
        else {
            favoritesService.addFavorite(userId, businessId);
        }
    }
}