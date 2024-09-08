package com.example.SomeOne.controller;

import com.example.SomeOne.dto.Businesses.request.FamousPlaceRequest;
import com.example.SomeOne.dto.Businesses.request.RecommendPlaceListRequest;
import com.example.SomeOne.dto.Businesses.response.PopularityPlaceResponse;
import com.example.SomeOne.dto.Businesses.response.RecommendPlaceResponse;
import com.example.SomeOne.service.PopularityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/popularity")
public class PopularityController {

    private final PopularityService popularityService;

    @GetMapping("/landmark/list") // 인기관광지 둘러보기
    public List<PopularityPlaceResponse> landmarkList(@RequestBody FamousPlaceRequest request) {
        return popularityService.listLandmark(request.getIslandId());
    }

    @GetMapping("/recommend/place") // 추천장소 리스트
    public List<PopularityPlaceResponse> recommendPlaceList(@RequestBody RecommendPlaceListRequest request) {
        return popularityService.recommendPlaceList(request.getIslandId(), request.getCategory());
    }

}
