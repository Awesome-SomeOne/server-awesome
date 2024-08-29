package com.example.SomeOne.dto.TravelRecords.Request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTravelRecordRequest {

    private Long planId;

    @Size(max = 25)
    private String oneLineReview; // 한 줄 리뷰

    @Size(max = 500)
    private String overallReview; // 상세 리뷰

    private boolean publicPrivate; // 공개 여부

    // 섬 리뷰 관련 필드 추가
    private Long islandId; // 섬 ID
    @Max(5) // 별점은 1부터 5까지
    private Integer rating; // 별점
    private String shortReview; // 한줄평
    private String detailedReview; // 상세 리뷰
}
