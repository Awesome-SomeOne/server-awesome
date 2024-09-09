package com.example.SomeOne.repository;

import com.example.SomeOne.domain.TravelPlace;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TravelPlaceRepository extends JpaRepository<TravelPlace, Long> {
    List<TravelPlace> findAllByTravelPlans_PlanIdAndDate(Long planId, LocalDate date);
    List<TravelPlace> findAllByTravelPlans_PlanIdAndDateOrderByPlaceOrderAsc(Long placeId, LocalDate date);
    List<TravelPlace> findAllByTravelPlans_PlanIdOrderByDateAsc(Long placeId);
    List<TravelPlace> findByTravelPlans_User(Users user);

    @Query("SELECT tp FROM TravelPlace tp " +
            "JOIN tp.travelPlans tp2 " +
            "JOIN tp2.user u " +
            "JOIN tp.businesses b " +
            "WHERE u.id = :userId " +
            "AND b.business_name LIKE %:businessName%")
    List<TravelPlace> findByUserIdAndBusinessNameContaining(@Param("userId") Long userId, @Param("businessName") String businessName);
}


