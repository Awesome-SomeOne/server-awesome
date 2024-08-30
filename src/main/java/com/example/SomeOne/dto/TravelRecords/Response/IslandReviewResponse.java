package com.example.SomeOne.dto.TravelRecords.Response;

import com.example.SomeOne.domain.IslandReviews;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IslandReviewResponse {
    private Long reviewId;
    private int rating;
    private String shortReview;
    private String detailedReview;

    public IslandReviewResponse(IslandReviews review) {
        if (review != null) {
            this.reviewId = review.getReviewId();
            this.rating = review.getRating();
            this.shortReview = review.getShortReview();
            this.detailedReview = review.getDetailedReview();
        }
    }
}
