package com.example.SomeOne.dto.TravelRecords.Response;

import com.example.SomeOne.dto.Businesses.response.BusinessReviewResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TravelRecordResponse {

    private Long recordId;
    private String recordTitle;
    private String recordContent;
    private List<String> imageUrls;
    private boolean publicPrivate;
    private Long planId;
    private Long userId;
    private Map<LocalDate, List<BusinessReviewResponse>> businessReviewsByDate;

    @Builder
    public TravelRecordResponse(Long recordId, String recordTitle, String recordContent, List<String> imageUrls, boolean publicPrivate, Long planId, Long userId, Map<LocalDate, List<BusinessReviewResponse>> businessReviewsByDate) {
        this.recordId = recordId;
        this.recordTitle = recordTitle;
        this.recordContent = recordContent;
        this.imageUrls = imageUrls;
        this.publicPrivate = publicPrivate;
        this.planId = planId;
        this.userId = userId;
        this.businessReviewsByDate = businessReviewsByDate;
    }
}
