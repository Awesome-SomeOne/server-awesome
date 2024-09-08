package com.example.SomeOne.controller;

import com.example.SomeOne.domain.IslandReviews;
import com.example.SomeOne.dto.TravelRecords.Request.CreateIslandReviewRequest;
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
    public ResponseEntity<IslandReviews> getIslandReview(@PathVariable Long islandId, @PathVariable Long userId) {
        IslandReviews islandReview = islandReviewsService.getIslandReview(islandId, userId);
        return ResponseEntity.ok(islandReview);
    }

    // 리뷰 생성 또는 수정
    @PostMapping("/createOrUpdate")
    public ResponseEntity<IslandReviews> createOrUpdateIslandReview(@RequestBody CreateIslandReviewRequest request) {
        IslandReviews islandReview = islandReviewsService.createOrUpdateIslandReview(request);
        return ResponseEntity.ok(islandReview);
    }

    // 리뷰 삭제
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Void> deleteIslandReview(@PathVariable Long reviewId) {
        islandReviewsService.deleteIslandReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}