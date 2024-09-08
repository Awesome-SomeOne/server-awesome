package com.example.SomeOne.controller;

import com.example.SomeOne.domain.IslandReviews;
import com.example.SomeOne.dto.TravelRecords.Request.CreateIslandReviewRequest;
import com.example.SomeOne.dto.TravelRecords.Response.IslandReviewResponse;
import com.example.SomeOne.service.IslandReviewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/island-reviews")
@RequiredArgsConstructor
public class IslandReviewsController {

    private final IslandReviewsService islandReviewsService;

    // 리뷰 조회
    @GetMapping("/view/{islandId}/{userId}")
    public ResponseEntity<IslandReviewResponse> getIslandReview(@PathVariable Long islandId, @PathVariable Long userId) {
        IslandReviewResponse response = islandReviewsService.getIslandReview(islandId, userId);
        return ResponseEntity.ok(response);
    }

    // 리뷰 생성 또는 수정
    @PostMapping("/createOrUpdate")
    public ResponseEntity<IslandReviewResponse> createOrUpdateIslandReview(@RequestBody CreateIslandReviewRequest request) {
        IslandReviewResponse response = islandReviewsService.createOrUpdateIslandReview(request);
        return ResponseEntity.ok(response);
    }

    // 리뷰 삭제
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Void> deleteIslandReview(@PathVariable Long reviewId) {
        islandReviewsService.deleteIslandReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
