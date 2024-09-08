package com.example.SomeOne.dto.TravelRecords.Response;

import com.example.SomeOne.domain.IslandReviews;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class IslandReviewResponse {
    private Long id;
    private Long islandId;
    private Long userId;
    private Integer rating;
    private String shortReview;
    private String detailedReview;

    // 기본 생성자
    public IslandReviewResponse() {}

    // All-args 생성자
    public IslandReviewResponse(Long id, Long islandId, Long userId, Integer rating, String shortReview, String detailedReview) {
        this.id = id;
        this.islandId = islandId;
        this.userId = userId;
        this.rating = rating;
        this.shortReview = shortReview;
        this.detailedReview = detailedReview;
    }

    // IslandReviews 객체를 이용한 생성자 (이 경우는 Builder를 사용하는 것이 좋음)
    public IslandReviewResponse(IslandReviews review) {
        this.id = review.getReviewId();
        this.islandId = review.getIsland().getId();
        this.userId = review.getUser().getUsers_id();
        this.rating = review.getRating();
        this.shortReview = review.getShortReview();
        this.detailedReview = review.getDetailedReview();
    }
}
