package com.example.SomeOne.service;

import com.example.SomeOne.domain.CommentLike;
import com.example.SomeOne.domain.Comments;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.repository.CommentLikeRepository;
import com.example.SomeOne.repository.CommentRepository;
import com.example.SomeOne.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentLikeService(CommentLikeRepository commentLikeRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.commentLikeRepository = commentLikeRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    // 댓글에 좋아요 추가
    public String addLike(Long commentId, Long userId) {
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentAndUser(comment, user);
        if (existingLike.isPresent()) {
            return "이미 좋아요를 눌렀습니다.";
        }

        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        commentLikeRepository.save(commentLike);

        return "좋아요가 성공적으로 추가되었습니다.";
    }

    // 댓글에 좋아요 취소
    public String removeLike(Long commentId, Long userId) {
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentAndUser(comment, user);
        if (existingLike.isEmpty()) {
            return "좋아요를 누르지 않았습니다.";
        }

        commentLikeRepository.delete(existingLike.get());

        return "좋아요가 성공적으로 취소되었습니다.";
    }
}
