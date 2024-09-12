package com.example.SomeOne.controller;

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

    //여행 기록 생성
    @PostMapping("/create")
    public TravelRecordResponse createTravelRecord(@RequestParam("images") List<MultipartFile> images,
                                                   @ModelAttribute CreateTravelRecordRequest request) {
        return travelRecordsService.create(images, request);
    }

    /**
     * 여행 기록 수정 API
     *
     * @param recordId 수정할 여행 기록의 ID
     * @param newImages 새로 업로드할 이미지 목록 (선택 사항)
     * @param request 여행 기록 수정에 필요한 데이터
     * @return 수정된 여행 기록의 응답 객체
     */
    @PostMapping("/update/{recordId}")
    public TravelRecordResponse updateTravelRecord(@PathVariable Long recordId,
                                                   @RequestParam(value = "images", required = false) List<MultipartFile> newImages,
                                                   @ModelAttribute CreateTravelRecordRequest request) {
        return travelRecordsService.update(recordId, request, newImages);
    }

    // 여행 기록 삭제
    @DeleteMapping("/delete/{recordId}")
    public void deleteTravelRecord(@PathVariable Long recordId) {
        travelRecordsService.delete(recordId);
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
        return travelRecordsService.getRecordById(recordId);
    }

    /**
     * 사용자별 여행 기록 조회 API
     *
     * @param userId 조회할 사용자의 ID
     * @return 조회된 여행 기록 목록
     */
    @GetMapping("/view-user/{userId}")
    public List<TravelRecordResponse> getRecordsByUser(@PathVariable Long userId) {
        return travelRecordsService.getRecordsByUser(userId);
    }

    /**
     * 여행 계획별 여행 기록 조회 API
     *
     * @param planId 조회할 여행 계획의 ID
     * @return 조회된 여행 기록 목록
     */
    @GetMapping("/view-plan/{planId}")
    public List<TravelRecordResponse> getRecordsByPlan(@PathVariable Long planId) {
        return travelRecordsService.getRecordsByPlan(planId);
    }

    /**
     * 사용자별 공개된 여행 기록 조회 API
     *
     * @param userId 조회할 사용자의 ID
     * @return 조회된 공개된 여행 기록 목록
     */
    @GetMapping("/view-user-true/{userId}")
    public List<TravelRecordResponse> getRecordsByUserTrue(@PathVariable Long userId) {
        return travelRecordsService.getRecordsByUserTrue(userId);
    }

    /**
     * 여행 계획별 공개된 여행 기록 조회 API
     *
     * @param planId 조회할 여행 계획의 ID
     * @return 조회된 공개된 여행 기록 목록
     */
    @GetMapping("/view-plan-true/{planId}")
    public List<TravelRecordResponse> getRecordsByPlanTrue(@PathVariable Long planId) {
        return travelRecordsService.getRecordsByPlanTrue(planId);
    }
}