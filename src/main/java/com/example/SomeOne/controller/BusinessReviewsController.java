package com.example.SomeOne.controller;

import com.example.SomeOne.config.SecurityUtil;
import com.example.SomeOne.dto.Businesses.request.CreateBusinessReviewRequest;
import com.example.SomeOne.dto.Businesses.response.BusinessReviewResponse;
import com.example.SomeOne.service.BusinessReviewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/business-reviews")
public class BusinessReviewsController {

    private final BusinessReviewsService businessReviewsService;

    @PostMapping("/createOrUpdate")
    public ResponseEntity<BusinessReviewResponse> create(
            @Validated @RequestPart("request") CreateBusinessReviewRequest request,
            @RequestPart("images") List<MultipartFile> images) {
        Long userId = SecurityUtil.getAuthenticatedUserId(); // JWT에서 사용자 ID 가져오기
        return ResponseEntity.ok(businessReviewsService.createOrUpdateBusinessReview(userId, request, images));
    }

    @GetMapping("/view/{businessId}")
    public ResponseEntity<BusinessReviewResponse> getReview(
            @PathVariable Long businessId) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        return ResponseEntity.ok(businessReviewsService.getBusinessReview(businessId, userId));
    }

    @DeleteMapping("/delete/{businessId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long businessId) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        businessReviewsService.deleteBusinessReview(businessId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<BusinessReviewResponse>> getAllReviews() {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        List<BusinessReviewResponse> response = businessReviewsService.getAllBusinessReviews(userId);
        return ResponseEntity.ok(response);
    }
}
