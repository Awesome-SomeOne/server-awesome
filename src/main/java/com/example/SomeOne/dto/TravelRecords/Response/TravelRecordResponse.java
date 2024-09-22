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
    private List<String> imageUrls;
    private boolean publicPrivate;
    private Long planId;
    private Long userId;
    private List<BusinessReviewResponse> businessReviews;

    public TravelRecordResponse(Long recordId, String oneLineReview, String overallReview, List<String> imageUrls, boolean publicPrivate, Long planId, Long userId, List<BusinessReviewResponse> businessReviews) {
        this.recordId = recordId;
        this.oneLineReview = oneLineReview;
        this.overallReview = overallReview;
        this.imageUrls = imageUrls;
        this.publicPrivate = publicPrivate;
        this.planId = planId;
        this.userId = userId;
        this.businessReviews = businessReviews;
    }
}

