package com.example.SomeOne.controller;

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
        return ResponseEntity.ok(businessReviewsService.createOrUpdateBusinessReview(request, images));
    }

    @GetMapping("/view/{businessId}/{userId}")
    public ResponseEntity<BusinessReviewResponse> getReview(
            @PathVariable Long businessId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(businessReviewsService.getBusinessReview(businessId, userId));
    }

    @DeleteMapping("/delete/{businessId}/{userId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long businessId,
            @PathVariable Long userId) {
        businessReviewsService.deleteBusinessReview(businessId, userId);
        return ResponseEntity.noContent().build();
    }
}