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
        SaveTravelResponse response = travelPlansService.save(request);
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
    public ResponseEntity<List<GetPlansResponse>> getPlans(@RequestParam Long userId) {
        List<GetPlansResponse> response = travelPlansService.getPlan(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plan")
    public ResponseEntity<GetTravelPlanResponse> getPlan(@RequestParam("planId") Long planId) {
        GetTravelPlanResponse response = travelPlansService.findTravelPlan(planId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addPlace")
    public ResponseEntity<Void> addPlace(@RequestBody AddPlaceRequest request) {
        travelPlaceService.addPlace(request.getTravelPlanId(), request.getBusinessId(), request.getDate());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addManyPlace")
    public ResponseEntity<Void> addManyPlace(@RequestBody AddManyPlaceRequest request) {
        travelPlaceService.addManyPlaces(request.getTravelPlanId(), request.getBusinessIds(), request.getDate());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletePlace")
    public ResponseEntity<Void> deletePlace(@RequestBody DeletePlaceRequest request) {
        travelPlaceService.deletePlace(request.getTravelPlaceId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/date")
    public ResponseEntity<Void> updateDate(@RequestBody UpdateDateRequest request) {
        travelPlaceService.updateDate(request.getTravelPlaceId(), request.getTravelPlanId(), request.getBusinessId(), request.getDate());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change/order")
    public ResponseEntity<Void> changeOrder(@RequestBody ChangeOrderRequest request) {
        travelPlaceService.changeOrder(request.getTravelPlaceId(), request.getOrder());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/place")
    public ResponseEntity<Void> updatePlace(@RequestBody List<UpdatePlaceRequest> request) {
        travelPlaceService.updatePlace(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/travel")
    public ResponseEntity<Void> deleteTravel(@RequestBody DeletePlanRequest request) {
        travelPlansService.delete(request.getPlanId());
        return ResponseEntity.ok().build();
    }
}
