package com.example.SomeOne.controller;

import com.example.SomeOne.dto.Businesses.response.FindBusinessesResponse;
import com.example.SomeOne.dto.TravelPlans.request.TravelPlanRequest;
import com.example.SomeOne.dto.TravelPlans.response.FindIslandResponse;
import com.example.SomeOne.service.BusinessesService;
import com.example.SomeOne.service.IslandService;
import com.example.SomeOne.service.TravelPlansService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel")
public class TravelController {

    private final IslandService islandService;
    private final TravelPlansService travelPlansService;
    private final BusinessesService businessesService;

    @GetMapping("/findIsland")
    public List<FindIslandResponse> findIsland(String keyword) {
        return islandService.findIsland(keyword);
    }

    @PostMapping("/save")
    public void savePlan(TravelPlanRequest request) {
        travelPlansService.save(request);
    }

    @GetMapping("/findPlace")
    public List<FindBusinessesResponse> findPlace(String keyword) {
        return businessesService.findBusinesses(keyword);
    }
}
