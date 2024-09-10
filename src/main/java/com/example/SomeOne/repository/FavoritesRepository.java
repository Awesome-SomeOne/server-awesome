package com.example.SomeOne.repository;

import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.Favorites;
import com.example.SomeOne.domain.FavoritesId;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, FavoritesId> {

    boolean existsByUserAndBusiness(Users user, Businesses business);

    Optional<Favorites> findByUserAndBusiness(Users user, Businesses business);

    List<Favorites> findByUser(Users user);
}
