package com.example.SomeOne.repository;

import com.example.SomeOne.domain.BusinessReviewImages;
import com.example.SomeOne.domain.BusinessReviews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BusinessReviewImagesRepository extends JpaRepository<BusinessReviewImages, Long> {
    List<BusinessReviewImages> findByReview(BusinessReviews review);
    // 날짜를 기준으로 리뷰 이미지를 조회하는 메서드
    //List<BusinessReviewImages> findByReviewAndDate(BusinessReviews review, LocalDate date);
}