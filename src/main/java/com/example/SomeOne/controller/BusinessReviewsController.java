package com.example.SomeOne.controller;


import com.example.SomeOne.dto.Businesses.request.CreateBusinessReviewRequest;
import com.example.SomeOne.dto.Businesses.response.BusinessReviewResponse;
import com.example.SomeOne.service.BusinessReviewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business-reviews")
@RequiredArgsConstructor
public class BusinessReviewsController {

    private final BusinessReviewsService businessReviewsService;

    // 비즈니스 리뷰 조회
    @GetMapping("/view/{businessId}/{userId}")
    public ResponseEntity<BusinessReviewResponse> getBusinessReview(@PathVariable Long businessId, @PathVariable Long userId) {
        BusinessReviewResponse response = businessReviewsService.getBusinessReview(businessId, userId);
        return ResponseEntity.ok(response);
    }

    // 비즈니스 리뷰 생성 또는 수정
    @PostMapping("/createOrUpdate")
    public ResponseEntity<BusinessReviewResponse> createOrUpdateBusinessReview(@RequestBody CreateBusinessReviewRequest request) {
        BusinessReviewResponse response = businessReviewsService.createOrUpdateBusinessReview(request);
        return ResponseEntity.ok(response);
    }

    // 비즈니스 리뷰 삭제
    @DeleteMapping("/delete/{businessId}/{userId}")
    public ResponseEntity<Void> deleteBusinessReview(@PathVariable Long businessId, @PathVariable Long userId) {
        businessReviewsService.deleteBusinessReview(businessId, userId);
        return ResponseEntity.noContent().build();
    }
}
