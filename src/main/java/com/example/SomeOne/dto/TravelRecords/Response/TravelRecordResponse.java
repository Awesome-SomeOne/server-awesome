package com.example.SomeOne.dto.TravelRecords.Response;

import com.example.SomeOne.domain.enums.TravelStatus;
import com.example.SomeOne.dto.Businesses.response.BusinessReviewResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class TravelRecordResponse {

    private Long recordId;
    private String recordTitle;
    private String recordContent;
    private List<String> imageUrls;
    private Boolean publicPrivate;

    // TravelPlans 관련 정보 추가
    private Long planId;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String islandName;  // Island 관련 정보
    private TravelStatus status;

    private Long userId;
    private Map<LocalDate, List<BusinessReviewResponse>> businessReviews; //날짜별 비즈니스 리뷰

    private Double latitude;
    private Double longitude;



    @Builder
    public TravelRecordResponse(Long recordId, String recordTitle, String recordContent, List<String> imageUrls, Boolean publicPrivate,
                                Long planId, String planName, LocalDate startDate, LocalDate endDate, String islandName, TravelStatus status,
                                Long userId, Map<LocalDate, List<BusinessReviewResponse>> businessReviews, Double latitude, Double longitude) {
        this.recordId = recordId;
        this.recordTitle = recordTitle;
        this.recordContent = recordContent;
        this.imageUrls = imageUrls;
        this.publicPrivate = publicPrivate;
        this.planId = planId;
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.islandName = islandName;
        this.status = status;
        this.userId = userId;
        this.businessReviews = businessReviews;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
