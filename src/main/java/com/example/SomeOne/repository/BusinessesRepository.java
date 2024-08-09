package com.example.SomeOne.repository;

import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.enums.Business_category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BusinessesRepository extends JpaRepository<Businesses, Long> {

    @Query("SELECT b FROM Businesses b WHERE b.business_name LIKE %:keyword%")
    List<Businesses> findByKeyword(@Param("keyword") String keyword);

    List<Businesses> findByIslandIdAndBusinessType(Long islandId, Business_category category);
}
