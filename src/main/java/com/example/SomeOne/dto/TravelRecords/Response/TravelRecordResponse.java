package com.example.SomeOne.dto.TravelRecords.Response;

import com.example.SomeOne.domain.TravelRecords;
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
    private IslandReviewResponse islandReview; // 섬 리뷰 정보를 담는 객체

    public TravelRecordResponse(TravelRecords record, String imageUrl, IslandReviewResponse islandReview) {
        this.recordId = record.getRecordId();
        this.oneLineReview = record.getRecordTitle();
        this.overallReview = record.getRecordContent();
        this.imageUrl = imageUrl;
        this.publicPrivate = record.getPublicPrivate();
        this.planId = record.getPlan().getPlanId();
        this.userId = record.getUser().getUsers_id();
        this.islandReview = islandReview;
    }
}
