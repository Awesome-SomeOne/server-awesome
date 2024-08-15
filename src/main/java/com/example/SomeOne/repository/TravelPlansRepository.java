package com.example.SomeOne.repository;

import com.example.SomeOne.domain.TravelPlans;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelPlansRepository extends JpaRepository<TravelPlans, Long> {
    List<TravelPlans> findByUserOrderByStartDateDesc(Users user);
}
