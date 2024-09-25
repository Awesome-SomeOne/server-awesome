package com.example.SomeOne.controller;

import com.example.SomeOne.config.SecurityUtil;
import com.example.SomeOne.dto.TravelRecords.Request.CreateTravelRecordRequest;
import com.example.SomeOne.dto.TravelRecords.Response.TravelRecordResponse;
import com.example.SomeOne.service.TravelRecordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/travel-records")
@RequiredArgsConstructor
public class TravelRecordsController {

    private final TravelRecordsService travelRecordsService;

    // 여행 기록 생성
    @PostMapping("/create")
    public TravelRecordResponse createTravelRecord(@RequestParam("images") List<MultipartFile> images,
                                                   @ModelAttribute CreateTravelRecordRequest request) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        return travelRecordsService.create(images, request, userId);
    }

    //여행 기록 수정
    @PostMapping("/update/{recordId}")
    public TravelRecordResponse updateTravelRecord(@PathVariable Long recordId,
                                                   @RequestParam(value = "images", required = false) List<MultipartFile> newImages,
                                                   @ModelAttribute CreateTravelRecordRequest request) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        return travelRecordsService.update(recordId, request, newImages, userId);
    }

    // 여행 기록 삭제
    @DeleteMapping("/delete/{recordId}")
    public void deleteTravelRecord(@PathVariable Long recordId) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        travelRecordsService.delete(recordId, userId);
    }

    // 공유 가능한 링크를 생성
    @GetMapping("/share/{recordId}")
    public String shareRecord(@PathVariable Long recordId) {
        // 여행 기록의 ID로 공유 가능한 URL 생성
        String shareableUrl = "http://awesome-island.duckdns.org/api/travel-records/view/" + recordId;
        return shareableUrl;
    }

    // 여행 기록 조회
    @GetMapping("/view/{recordId}")
    public TravelRecordResponse getRecordById(@PathVariable Long recordId) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        return travelRecordsService.getRecordById(recordId, userId);
    }

    // 사용자별 여행 기록 조회
    public List<TravelRecordResponse> getRecordsByUser() {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        return travelRecordsService.getRecordsByUser(userId);
    }

    //여행 기록별 여행 기록 조회
    @GetMapping("/view-plan/{planId}")
    public List<TravelRecordResponse> getRecordsByPlan(@PathVariable Long planId) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        return travelRecordsService.getRecordsByPlan(planId, userId);
    }

    // 사용자별 공개된 여행 기록 조회
    @GetMapping("/view-user-true")
    public List<TravelRecordResponse> getRecordsByUserTrue() {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        return travelRecordsService.getRecordsByUserTrue(userId);
    }

    //여행 기록별 공개된 여행 기록 조회
    @GetMapping("/view-plan-true/{planId}")
    public List<TravelRecordResponse> getRecordsByPlanTrue(@PathVariable Long planId) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        return travelRecordsService.getRecordsByPlanTrue(planId, userId);
    }
}