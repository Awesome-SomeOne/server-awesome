package com.example.SomeOne.controller;

import com.example.SomeOne.dto.TravelRecords.Request.CreateTravelRecordRequest;
import com.example.SomeOne.dto.TravelRecords.Response.TravelRecordResponse;
import com.example.SomeOne.service.TravelRecordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/travel-records")
@RequiredArgsConstructor
public class TravelRecordsController {

    private final TravelRecordsService travelRecordsService;

    // 여행 기록 생성
    @PostMapping("/create")
    public ResponseEntity<TravelRecordResponse> createTravelRecord(
            @RequestParam("images") List<MultipartFile> images,
            @ModelAttribute CreateTravelRecordRequest request) {

        // 이미지가 5개를 초과하는지 체크
        if (images.size() > 5) {
            return ResponseEntity.badRequest().body(null);
        }

        TravelRecordResponse response = travelRecordsService.create(images, request);
        return ResponseEntity.ok(response);
    }

    // 여행 기록 수정
    @PostMapping("/update/{recordId}")
    public ResponseEntity<TravelRecordResponse> updateTravelRecord(
            @PathVariable Long recordId,
            @RequestPart("request") CreateTravelRecordRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages) {
        TravelRecordResponse response = travelRecordsService.update(recordId, request, newImages);
        return ResponseEntity.ok(response);
    }

    // 여행 기록 삭제
    @DeleteMapping("/delete/{recordId}")
    public ResponseEntity<Map<String, String>> deleteTravelRecord(@PathVariable Long recordId) {
        travelRecordsService.delete(recordId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Travel record deleted successfully");
        return ResponseEntity.ok(response); // or ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    // 공유 가능한 링크를 생성하는 메서드
    @GetMapping("/share/{recordId}")
    public String shareRecord(@PathVariable Long recordId) {
        // 여행 기록의 ID로 공유 가능한 URL 생성
        String shareableUrl = "http://localhost:8080/api/travel-records/view/" + recordId;
        return shareableUrl;
    }

    // 여행 기록 조회
    @GetMapping("/view/{recordId}")
    public TravelRecordResponse viewRecord(@PathVariable Long recordId) {
        return travelRecordsService.getRecordById(recordId);
    }

    // 사용자별 여행 기록 조회
    @GetMapping("/view-user/{userId}")
    public ResponseEntity<List<TravelRecordResponse>> getRecordsByUser(@PathVariable Long userId) {
        List<TravelRecordResponse> records = travelRecordsService.getRecordsByUser(userId);
        return ResponseEntity.ok(records);
    }

    // 여행 계획별 여행 기록 조회
    @GetMapping("/view-plan/{planId}")
    public ResponseEntity<List<TravelRecordResponse>> getRecordsByPlan(@PathVariable Long planId) {
        List<TravelRecordResponse> records = travelRecordsService.getRecordsByPlan(planId);
        return ResponseEntity.ok(records);
    }

    // 사용자별 여행 기록 조회 - publicPrivate:true only
    @GetMapping("/view-user-true/{userId}")
    public ResponseEntity<List<TravelRecordResponse>> getRecordsByUserTrue(@PathVariable Long userId) {
        List<TravelRecordResponse> records = travelRecordsService.getRecordsByUserTrue(userId);
        return ResponseEntity.ok(records);

    }

    // 여행 계획별 여행 기록 조회 - publicPrivate:true only
    @GetMapping("/view-plan-true/{planId}")
    public ResponseEntity<List<TravelRecordResponse>> getRecordsByPlanTrue(@PathVariable Long planId) {
        List<TravelRecordResponse> records = travelRecordsService.getRecordsByPlanTrue(planId);
        return ResponseEntity.ok(records);
    }

}
