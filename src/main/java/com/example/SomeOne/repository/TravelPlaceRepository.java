package com.example.SomeOne.repository;

import com.example.SomeOne.domain.TravelPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TravelPlaceRepository extends JpaRepository<TravelPlace, Long> {
    List<TravelPlace> findAllByTravelPlans_PlanIdAndDate(Long planId, LocalDate date);
    List<TravelPlace> findAllByTravelPlans_PlanIdAndDateOrderByOrderAsc(Long placeId, LocalDate date);
}
