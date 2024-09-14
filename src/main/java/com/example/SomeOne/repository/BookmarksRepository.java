package com.example.SomeOne.repository;

import com.example.SomeOne.domain.Bookmarks;
import com.example.SomeOne.domain.CommunityPosts;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarksRepository extends JpaRepository<Bookmarks, Long> {
    Optional<Bookmarks> findByPostAndUser(CommunityPosts post, Users user); // 게시물과 사용자로 북마크 검색
    Long countByPost(CommunityPosts post); // 게시물에 대한 북마크 수
}
