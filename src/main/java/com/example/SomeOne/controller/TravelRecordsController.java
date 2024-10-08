package com.example.SomeOne.controller;

import com.example.SomeOne.config.SecurityUtil;
import com.example.SomeOne.dto.TravelRecords.Request.CreateTravelRecordRequest;
import com.example.SomeOne.dto.TravelRecords.Response.TravelRecordResponse;
import com.example.SomeOne.service.TravelRecordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/travel-records")
@RequiredArgsConstructor
public class TravelRecordsController {

    private final TravelRecordsService travelRecordsService;

    @PostMapping("/create")
    public ResponseEntity<?> createTravelRecord(@RequestParam("images") List<MultipartFile> images,
                                                @ModelAttribute CreateTravelRecordRequest request) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        try {
            TravelRecordResponse response = travelRecordsService.create(images, request, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Global 예외 처리 핸들러
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
    @GetMapping("/view-user")
    public List<TravelRecordResponse> getRecordsByUser() {
        return travelRecordsService.getRecordsByUser();
    }

    // 여행 계획별 여행 기록 조회
    @GetMapping("/view-plan/{planId}")
    public List<TravelRecordResponse> getRecordsByPlan(@PathVariable Long planId) {
        return travelRecordsService.getRecordsByPlan(planId);
    }

    // 사용자별 공개된 여행 기록 조회
    @GetMapping("/view-user-true")
    public List<TravelRecordResponse> getRecordsByUserTrue() {
        return travelRecordsService.getRecordsByUserTrue();
    }

    // 여행 계획별 공개된 여행 기록 조회
    @GetMapping("/view-plan-true/{planId}")
    public List<TravelRecordResponse> getRecordsByPlanTrue(@PathVariable Long planId) {
        return travelRecordsService.getRecordsByPlanTrue(planId);
    }

}