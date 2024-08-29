package com.example.SomeOne.dto.TravelRecords.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateIslandReviewRequest {
    private Long islandId; // 섬 ID 추가
    private Long userId;
    private int rating;
    private String shortReview;
    private String detailedReview;
}