package com.example.SomeOne.controller;

import com.example.SomeOne.dto.Businesses.request.FindBusinessesRequest;
import com.example.SomeOne.dto.Businesses.request.RecommendPlaceRequest;
import com.example.SomeOne.dto.Businesses.response.FindBusinessesResponse;
import com.example.SomeOne.dto.Businesses.response.RecommendPlaceResponse;
import com.example.SomeOne.dto.TravelPlans.request.*;
import com.example.SomeOne.dto.TravelPlans.response.FindIslandResponse;
import com.example.SomeOne.dto.TravelPlans.response.GetPlanResponse;
import com.example.SomeOne.service.BusinessesService;
import com.example.SomeOne.service.IslandService;
import com.example.SomeOne.service.TravelPlaceService;
import com.example.SomeOne.service.TravelPlansService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel")
public class TravelController {

    private final IslandService islandService;
    private final TravelPlansService travelPlansService;
    private final BusinessesService businessesService;
    private final TravelPlaceService travelPlaceService;

    @GetMapping("/findIsland")
    public List<FindIslandResponse> findIsland(@RequestBody FindIslandRequest request) {
        return islandService.findIsland(request.getKeyword());
    }

    @PostMapping("/save")
    public void savePlan(@RequestBody TravelPlanRequest request) {
        travelPlansService.save(request);
    }

    @GetMapping("/findPlace")
    public List<FindBusinessesResponse> findPlace(@RequestBody FindBusinessesRequest request) {
        return businessesService.findBusinesses(request.getKeyword());
    }

    @GetMapping("/recommend/place")
    public List<RecommendPlaceResponse> recommendPlace(@RequestBody RecommendPlaceRequest request) {
        return businessesService.recommendPlace(request.getIslandId(), request.getCategory());
    }

    @GetMapping("/plan")
    public List<GetPlanResponse> getPlan(@RequestBody Long userId) {
        // login 관련해서 다른 개발자와 코드 merge 후 수정
        return travelPlansService.getPlan(userId);
    }

    @PostMapping("/addPlace")
    public void addPlace(@RequestBody AddPlaceRequest request) {
        travelPlaceService.addPlace(request.getTravelPlanId(), request.getBusinessId(), request.getDate());
    }

    @DeleteMapping("/deletePlace")
    public void deletePlace(@RequestBody DeletePlaceRequest request) {
        travelPlaceService.deletePlace(request.getTravelPlaceId());
    }

    @PostMapping("/update/date")
    public void updateDate(@RequestBody UpdateDateRequest request) {
        travelPlaceService.updateDate(request.getTravelPlaceId(), request.getTravelPlanId(), request.getBusinessId(),
                request.getDate());
    }

    @DeleteMapping("/delete/travel")
    public void deleteTravel(@RequestBody DeletePlanRequest request) {
        travelPlansService.delete(request.getPlanId());
    }
}
