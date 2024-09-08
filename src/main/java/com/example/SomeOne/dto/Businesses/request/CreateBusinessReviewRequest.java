package com.example.SomeOne.dto.Businesses.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBusinessReviewRequest {
    private Long businessId;
    private Long userId;
    private Integer rating;
    private String shortReview;
    private String detailedReview;
}
