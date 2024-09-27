package com.example.SomeOne.repository;

import com.example.SomeOne.domain.BusinessReviews;
import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.TravelRecords;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BusinessReviewsRepository extends JpaRepository<BusinessReviews, Long> {

    Optional<BusinessReviews> findByBusinessAndUser(Businesses business, Users user);
    void deleteByTravelRecord(TravelRecords travelRecord);

    Optional<BusinessReviews> findFirstByBusinessAndUser(Businesses business, Users user);
    @Query("SELECT r FROM BusinessReviews r WHERE r.business.business_id = :businessId")
    List<BusinessReviews> findAllByBusinessId(@Param("businessId") Long businessId);

    List<BusinessReviews> findByUser(Users user);

    // 비즈니스, 유저, 날짜를 기준으로 리뷰를 조회하는 JPQL 쿼리 수정
    @Query("SELECT br FROM BusinessReviews br JOIN br.travelRecord tr JOIN tr.plan tp JOIN tp.travelPlaces t " +
            "WHERE br.business = :business AND br.user = :user AND t.date = :date")
    Optional<BusinessReviews> findByBusinessAndUserAndDate(@Param("business") Businesses business,
                                                           @Param("user") Users user,
                                                           @Param("date") LocalDate date);

    //Optional<BusinessReviews> findByBusinessAndUserAndDate(Businesses business, Users user, LocalDate date);
}
