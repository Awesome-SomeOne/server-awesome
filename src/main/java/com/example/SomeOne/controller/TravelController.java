package com.example.SomeOne.controller;

import com.example.SomeOne.dto.Businesses.response.FamousPlaceResponse;
import com.example.SomeOne.dto.Businesses.response.FindBusinessesResponse;
import com.example.SomeOne.dto.Businesses.response.RecommendPlaceResponse;
import com.example.SomeOne.dto.TravelPlans.response.*;
import com.example.SomeOne.dto.TravelPlans.request.*;
import com.example.SomeOne.service.BusinessesService;
import com.example.SomeOne.service.IslandService;
import com.example.SomeOne.service.TravelPlaceService;
import com.example.SomeOne.service.TravelPlansService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.SomeOne.config.SecurityUtil.getAuthenticatedUserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel")
public class TravelController {

    private final IslandService islandService;
    private final TravelPlansService travelPlansService;
    private final BusinessesService businessesService;
    private final TravelPlaceService travelPlaceService;

    @GetMapping("/findIsland")
    public ResponseEntity<List<FindIslandResponse>> findIsland(@RequestParam("keyword") String keyword) {
        List<FindIslandResponse> response = islandService.findIsland(keyword);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<SaveTravelResponse> savePlan(@RequestBody TravelPlanRequest request) {
        Long userId = getAuthenticatedUserId();
        SaveTravelResponse response = travelPlansService.save(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findPlace")
    public ResponseEntity<List<FindBusinessesResponse>> findPlace(@RequestParam("keyword") String keyword) {
        List<FindBusinessesResponse> response = businessesService.findBusinesses(keyword);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommend/place")
    public ResponseEntity<List<RecommendPlaceResponse>> recommendPlace(@RequestParam("islandId") Long islandId,
                                                                       @RequestParam("category") String category) {
        List<RecommendPlaceResponse> response = businessesService.recommendPlace(islandId, category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommend/island")
    public ResponseEntity<RandomIslandResponse> recommendIsland() {
        RandomIslandResponse response = islandService.randomIsland();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/famous/place")
    public ResponseEntity<List<FamousPlaceResponse>> famousPlace(@RequestParam("islandId") Long islandId) {
        List<FamousPlaceResponse> response = businessesService.famousPlace(islandId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plans")
    public ResponseEntity<List<GetPlansResponse>> getPlans() {
        Long userId = getAuthenticatedUserId();
        List<GetPlansResponse> response = travelPlansService.getPlan(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plan")
    public ResponseEntity<GetTravelPlanResponse> getPlan(@RequestParam("planId") Long planId) {
        Long userId = getAuthenticatedUserId();
        GetTravelPlanResponse response = travelPlansService.findTravelPlan(userId, planId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addPlace")
    public ResponseEntity<AddPlaceResponse> addPlace(@RequestBody AddPlaceRequest request) {
        Long userId = getAuthenticatedUserId();
        AddPlaceResponse placeId = travelPlaceService.addPlace(userId, request.getTravelPlanId(), request.getBusinessId(), request.getDate());
        return ResponseEntity.ok(placeId);
    }

    @PostMapping("/addManyPlace")
    public ResponseEntity<AddManyPlaceResponse> addManyPlace(@RequestBody AddManyPlaceRequest request) {
        Long userId = getAuthenticatedUserId();
        AddManyPlaceResponse placeIds = travelPlaceService.addManyPlaces(userId,
                request.getTravelPlanId(), request.getBusinessIds(), request.getDate());
        return ResponseEntity.ok(placeIds);
    }

    @DeleteMapping("/deletePlace")
    public ResponseEntity<Void> deletePlace(@RequestBody DeletePlaceRequest request) {
        Long userId = getAuthenticatedUserId();
        travelPlaceService.deletePlace(userId, request.getTravelPlaceId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/date")
    public ResponseEntity<Void> updateDate(@RequestBody UpdateDateRequest request) {
        Long userId = getAuthenticatedUserId();
        travelPlaceService.updateDate(userId, request.getTravelPlaceId(), request.getTravelPlanId(),
                request.getBusinessId(), request.getDate());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change/order")
    public ResponseEntity<Void> changeOrder(@RequestBody ChangeOrderRequest request) {
        Long userId = getAuthenticatedUserId();
        travelPlaceService.changeOrder(userId, request.getTravelPlaceId(), request.getOrder());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/place")
    public ResponseEntity<Void> updatePlace(@RequestBody List<UpdatePlaceRequest> request) {
        Long userId = getAuthenticatedUserId();
        travelPlaceService.updatePlace(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/travel")
    public ResponseEntity<Void> deleteTravel(@RequestBody DeletePlanRequest request) {
        Long userId = getAuthenticatedUserId();
        travelPlansService.delete(userId, request.getPlanId());
        return ResponseEntity.ok().build();
    }
}
