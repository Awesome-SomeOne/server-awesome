package com.example.SomeOne.repository;

import com.example.SomeOne.domain.CommunityPosts;
import com.example.SomeOne.domain.Favorites;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    Optional<Favorites> findByPostAndUser(CommunityPosts post, Users user);
    Long countByPost(CommunityPosts post);  // 게시물의 좋아요 수를 계산하는 메서드
}
