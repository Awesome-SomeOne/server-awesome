package com.example.SomeOne.repository;

import com.example.SomeOne.domain.CommunityPosts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostsRepository extends JpaRepository<CommunityPosts, Long> {

}
