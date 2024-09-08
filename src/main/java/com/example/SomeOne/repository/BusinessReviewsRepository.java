package com.example.SomeOne.repository;

import com.example.SomeOne.domain.BusinessReviews;
import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessReviewsRepository extends JpaRepository<BusinessReviews, Long> {
    Optional<BusinessReviews> findByBusinessAndUser(Businesses business, Users user);
    void deleteByBusinessAndUser(Businesses business, Users user);
}
