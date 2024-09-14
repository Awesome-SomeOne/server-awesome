package com.example.SomeOne.controller;

import com.example.SomeOne.domain.enums.Business_category;
import com.example.SomeOne.dto.Businesses.request.FamousPlaceRequest;
import com.example.SomeOne.dto.Businesses.request.GetLandmarkRequest;
import com.example.SomeOne.dto.Businesses.request.RecommendPlaceListRequest;
import com.example.SomeOne.dto.Businesses.response.GetLandmarkResponse;
import com.example.SomeOne.dto.Businesses.response.PopularityPlaceResponse;
import com.example.SomeOne.service.PopularityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/popularity")
public class PopularityController {

    private final PopularityService popularityService;

    @GetMapping("/landmark/list") // 인기관광지 둘러보기
    public List<PopularityPlaceResponse> landmarkList(@RequestParam("islandId") Long islandId) {
        return popularityService.listLandmark(islandId);
    }

    @GetMapping("/recommend/place") // 추천장소 리스트
    public List<PopularityPlaceResponse> recommendPlaceList(@RequestParam("islandId") Long islandId,
                                                            @RequestParam("category") Business_category category) {
        return popularityService.recommendPlaceList(islandId, category);
    }

    @GetMapping("/landmark")
    public GetLandmarkResponse getLandmark(@RequestParam("businessId") Long businessId) {
        return popularityService.getPlace(businessId);
    }

}