package com.example.SomeOne.dto.Businesses.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BusinessReviewResponse {

    private Long id;
    private Long businessId;
    private Long userId;
    private Integer rating;
    private String shortReview;
    private String detailedReview;
    private List<String> imageUrls;
}