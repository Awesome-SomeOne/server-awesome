package com.example.SomeOne.dto.Businesses.response;

import com.example.SomeOne.domain.BusinessReviewImages;
import com.example.SomeOne.domain.BusinessReviews;
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
    private List<String> imageUrls;

    // 엔티티에서 DTO로 변환하는 메서드 추가
    public static BusinessReviewResponse fromEntity(BusinessReviews review, List<BusinessReviewImages> reviewImages) {
        List<String> imageUrls = reviewImages.stream()
                .map(BusinessReviewImages::getImageUrl)
                .collect(Collectors.toList());

        return BusinessReviewResponse.builder()
                .id(review.getReviewId())
                .businessId(review.getBusiness().getBusiness_id())
                .userId(review.getUser().getUsers_id())
                .rating(review.getRating())
                .businessReview(review.getBusinessReview())
                .imageUrls(imageUrls)
                .build();
    }
}
