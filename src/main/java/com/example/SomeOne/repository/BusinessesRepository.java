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

    List<Businesses> findByIslandId(Long islandId);

    @Query("SELECT b FROM Businesses b LEFT JOIN BusinessReviews r ON b.business_id = r.business.business_id " +
            "WHERE b.island.id = :islandId AND b.businessType = :category GROUP BY b.business_id ORDER BY AVG(r.rating) DESC")
    List<Businesses> findByIslandIdAndBusinessTypeOrderByRatingDesc(
            @Param("islandId") Long islandId,
            @Param("category") Business_category category);

    @Query("SELECT AVG(r.rating) FROM BusinessReviews r WHERE r.business.business_id = :businessId")
    Double findAverageRatingByBusinessId(@Param("businessId") Long businessId);

}
