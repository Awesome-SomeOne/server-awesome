package com.example.SomeOne.repository;

import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.TravelPlace;
import com.example.SomeOne.domain.TravelPlans;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TravelPlaceRepository extends JpaRepository<TravelPlace, Long> {
    List<TravelPlace> findAllByTravelPlans_PlanIdAndDate(Long planId, LocalDate date);
    List<TravelPlace> findAllByTravelPlans_PlanIdAndDateOrderByPlaceOrderAsc(Long placeId, LocalDate date);
    @Query("SELECT tp FROM TravelPlace tp WHERE tp.travelPlans.planId = :planId AND tp.travelPlans.user = :user ORDER BY tp.date ASC")
    List<TravelPlace> findAllByTravelPlans_PlanIdAndUserOrderByDateAsc(@Param("planId") Long planId, @Param("user") Users user);
    List<TravelPlace> findByTravelPlans_User(Users user);

    @Query("SELECT tp FROM TravelPlace tp " +
            "JOIN tp.travelPlans tp2 " +
            "JOIN tp2.user u " +
            "JOIN tp.businesses b " +
            "WHERE u.id = :userId " +
            "AND b.business_name LIKE %:businessName%")
    List<TravelPlace> findByUserIdAndBusinessNameContaining(@Param("userId") Long userId, @Param("businessName") String businessName);
    List<TravelPlace> findByTravelPlans(TravelPlans travelPlans);
    // 비즈니스와 유저를 기반으로 TravelPlace 엔티티 조회
    List<TravelPlace> findByBusinessesAndTravelPlans_User(Businesses business, Users user);
}


