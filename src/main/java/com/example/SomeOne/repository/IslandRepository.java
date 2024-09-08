package com.example.SomeOne.repository;

import com.example.SomeOne.domain.Island;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IslandRepository extends JpaRepository<Island, Long> {

    @Query("SELECT i FROM Island i WHERE i.name LIKE %:keyword%")
    List<Island> findByKeyword(@Param("keyword") String keyword);

    Island findByName(String name);
}
