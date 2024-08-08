package com.example.SomeOne.controller;

import com.example.SomeOne.domain.Island;
import com.example.SomeOne.dto.TravelPlans.request.TravelPlanRequest;
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

    @GetMapping("/findIsland")
    public List<String> findIsland(String keyword) {
        return islandService.findIsland(keyword);
    }

    @PostMapping("/save")
    public void savePlan(TravelPlanRequest request) {
        travelPlansService.save(request);
    }
}
