package com.example.SomeOne.repository;

import com.example.SomeOne.domain.TravelPlans;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TravelPlansRepository extends JpaRepository<TravelPlans, Long> {
    List<TravelPlans> findByUserOrderByStartDateDesc(Users user);
    List<TravelPlans> findByStartDate(LocalDate startDate);
    List<TravelPlans> findByEndDate(LocalDate endDate);
}
