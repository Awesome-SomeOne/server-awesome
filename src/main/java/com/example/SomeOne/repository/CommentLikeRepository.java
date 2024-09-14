package com.example.SomeOne.repository;

import com.example.SomeOne.domain.CommentLike;
import com.example.SomeOne.domain.Comments;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByCommentAndUser(Comments comment, Users user);
    Long countByComment(Comments comment);
}
