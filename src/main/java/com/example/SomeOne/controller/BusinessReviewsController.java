package com.example.SomeOne.controller;

import com.example.SomeOne.config.SecurityUtil;
import com.example.SomeOne.domain.enums.ReportReason;
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
    public ResponseEntity<?> getReview(@PathVariable Long businessId) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        BusinessReviewResponse reviewResponse = businessReviewsService.getBusinessReview(businessId, userId);

        if (reviewResponse == null) {
            return ResponseEntity.status(404).body("리뷰가 없습니다.");
        }
        return ResponseEntity.ok(reviewResponse);
    }

    @DeleteMapping("/delete/{businessId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long businessId) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        businessReviewsService.deleteBusinessReview(businessId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllReviews() {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        List<BusinessReviewResponse> response = businessReviewsService.getAllBusinessReviews(userId);

        if (response.isEmpty()) {
            return ResponseEntity.status(404).body("리뷰가 없습니다.");
        }

        return ResponseEntity.ok(response);
    }

    // 리뷰 신고
    @PostMapping("/report/{reviewId}")
    public ResponseEntity<?> reportReview(@PathVariable Long reviewId, @RequestParam ReportReason reportReason) {
        Long userId = SecurityUtil.getAuthenticatedUserId(); // JWT에서 사용자 ID 가져오기
        boolean isReported = businessReviewsService.reportReview(reviewId, userId, reportReason);

        if (!isReported) {
            return ResponseEntity.status(404).body("리뷰를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok("리뷰가 성공적으로 신고되었습니다. 신고 사유: " + reportReason);
    }
}
