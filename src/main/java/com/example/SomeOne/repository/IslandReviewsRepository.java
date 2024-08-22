package com.example.SomeOne.repository;

import com.example.SomeOne.domain.Island;
import com.example.SomeOne.domain.IslandReviews;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IslandReviewsRepository extends JpaRepository<IslandReviews, Long> {
    Optional<IslandReviews> findByIslandAndUser(Island island, Users user);
}