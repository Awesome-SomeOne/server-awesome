package com.example.SomeOne.repository;

import com.example.SomeOne.domain.BusinessReviewImages;
import com.example.SomeOne.domain.BusinessReviews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessReviewImagesRepository extends JpaRepository<BusinessReviewImages, Long> {
    List<BusinessReviewImages> findByReview(BusinessReviews review);
}