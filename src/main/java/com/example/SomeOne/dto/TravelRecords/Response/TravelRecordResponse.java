package com.example.SomeOne.dto.TravelRecords.Response;

import com.example.SomeOne.domain.TravelRecords;
import com.example.SomeOne.domain.IslandReviews;
import lombok.Getter;

@Getter
public class TravelRecordResponse {

    private Long recordId;
    private String oneLineReview;
    private String overallReview;
    private String imageUrl;
    private boolean publicPrivate;
    private Long planId;
    private Long userId;
    private Long islandReviewId; // 섬 리뷰 ID 추가
    private String islandReviewShortReview; // 섬 리뷰의 짧은 리뷰 추가
    private String islandReviewDetailedReview; // 섬 리뷰의 자세한 리뷰 추가

    public TravelRecordResponse(TravelRecords record, String imageUrl, Long islandReviewId, String islandReviewShortReview, String islandReviewDetailedReview) {
        this.recordId = record.getRecordId();
        this.oneLineReview = record.getRecordTitle();
        this.overallReview = record.getRecordContent();
        this.imageUrl = imageUrl;
        this.publicPrivate = record.getPublicPrivate();
        this.planId = record.getPlan().getPlanId();
        this.userId = record.getUser().getUsers_id();
        this.islandReviewId = islandReviewId;
        this.islandReviewShortReview = islandReviewShortReview;
        this.islandReviewDetailedReview = islandReviewDetailedReview;
    }

    @Getter
    public static class IslandReviewResponse {
        private Long reviewId;
        private int rating;
        private String shortReview;
        private String detailedReview;

        public IslandReviewResponse(IslandReviews review) {
            this.reviewId = review.getReviewId();
            this.rating = review.getRating();
            this.shortReview = review.getShortReview();
            this.detailedReview = review.getDetailedReview();
        }
    }
}
