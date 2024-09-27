package com.example.SomeOne.dto.Businesses.response;

import com.example.SomeOne.domain.BusinessReviewImages;
import com.example.SomeOne.domain.BusinessReviews;
import com.example.SomeOne.dto.TravelRecords.TravelDateImages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BusinessReviewResponse {

    private Long id;
    private Long businessId;
    private Long userId;
    private Integer rating;
    private String businessReview;
    private List<String> imageUrls; // 기존 이미지 URL 리스트
    private String xAddress; // x 좌표
    private String yAddress; // y 좌표
    private List<TravelDateImages> travelDateImages; // 날짜별 이미지 리스트

    // 엔티티에서 DTO로 변환하는 메서드
    public static BusinessReviewResponse fromEntity(BusinessReviews review, List<BusinessReviewImages> reviewImages, List<TravelDateImages> travelDateImages) {
        // 전체 이미지 URL 리스트 생성
        List<String> imageUrls = reviewImages.stream()
                .map(BusinessReviewImages::getImageUrl)
                .collect(Collectors.toList());

        // Businesses 엔티티에서 x, y 좌표를 가져옴
        String xAddress = review.getBusiness().getX_address();
        String yAddress = review.getBusiness().getY_address();

        // DTO 빌드
        return BusinessReviewResponse.builder()
                .id(review.getReviewId())
                .businessId(review.getBusiness().getBusiness_id())
                .userId(review.getUser().getUsers_id())
                .rating(review.getRating())
                .businessReview(review.getBusinessReview())
                .imageUrls(imageUrls)  // 기존 이미지 URL 리스트 설정
                .xAddress(xAddress)    // x 좌표 설정
                .yAddress(yAddress)    // y 좌표 설정
                .travelDateImages(travelDateImages) // 날짜별 이미지 리스트 설정
                .build();
    }
}

