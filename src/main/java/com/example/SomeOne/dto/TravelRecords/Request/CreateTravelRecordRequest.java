package com.example.SomeOne.dto.TravelRecords.Request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class CreateTravelRecordRequest {

    private Long planId;

    @Size(max = 25)
    private String oneLineReview; // 한 줄 리뷰

    @Size(max = 500)
    private String overallReview; // 상세 리뷰

    private boolean publicPrivate; // 공개 여부

    // 이미지 관련 필드
    private List<MultipartFile> newImages; // 새 이미지 목록

    // 비즈니스 리뷰 관련 필드
    private List<Long> businessIds; // 비즈니스 ID 목록
    private Integer rating; // 별점 (1부터 5까지)
    private String businessReview; // 비즈니스 리뷰 내용
    private List<MultipartFile> businessReviewImages; // 비즈니스 리뷰 이미지
}
