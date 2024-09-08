package com.example.SomeOne.repository;

import com.example.SomeOne.domain.BusinessReviews;
import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BusinessReviewsRepository extends JpaRepository<BusinessReviews, Long> {
    Optional<BusinessReviews> findByBusinessAndUser(Businesses business, Users user);
    void deleteByBusinessAndUser(Businesses business, Users user);
    @Query("SELECT r FROM BusinessReviews r WHERE r.business.business_id = :businessId")
    List<BusinessReviews> findAllByBusinessId(@Param("businessId") Long businessId);
}