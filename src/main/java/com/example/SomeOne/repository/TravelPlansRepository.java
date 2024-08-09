package com.example.SomeOne.repository;

import com.example.SomeOne.domain.TravelPlans;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelPlansRepository extends JpaRepository<TravelPlans, Long> {
    List<TravelPlans> findByUser_IdOrderByStart_dateDesc(Long userId);
}
