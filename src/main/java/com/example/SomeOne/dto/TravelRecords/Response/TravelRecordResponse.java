package com.example.SomeOne.dto.TravelRecords.Response;

import com.example.SomeOne.dto.Businesses.response.BusinessReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TravelRecordResponse {

    private Long recordId;
    private String oneLineReview;
    private String overallReview;
    private String imageUrl;
    private boolean publicPrivate;
    private Long planId;
    private Long userId;
    private List<BusinessReviewResponse> businessReviews; // 비즈니스 리뷰 리스트 추가

    // 기존 필드 유지하면서, businessReviews는 리스트 형태로 추가
    public TravelRecordResponse(Long recordId, String oneLineReview, String overallReview, String imageUrl, boolean publicPrivate, Long planId, Long userId, List<BusinessReviewResponse> businessReviews) {
        this.recordId = recordId;
        this.oneLineReview = oneLineReview;
        this.overallReview = overallReview;
        this.imageUrl = imageUrl;
        this.publicPrivate = publicPrivate;
        this.planId = planId;
        this.userId = userId;
        this.businessReviews = businessReviews;
    }
}
