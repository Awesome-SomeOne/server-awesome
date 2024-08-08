package com.example.SomeOne.repository;

import com.example.SomeOne.domain.TravelPlans;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelPlansRepository extends JpaRepository<TravelPlans, Long> {
}
