package com.example.SomeOne.service;

import com.example.SomeOne.domain.Comments;
import com.example.SomeOne.domain.CommunityPosts;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.dto.Comment.request.CommentRequest;
import com.example.SomeOne.dto.Comment.response.CommentResponse;
import com.example.SomeOne.repository.CommentRepository;
import com.example.SomeOne.repository.CommunityPostsRepository;
import com.example.SomeOne.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityPostsRepository communityPostsRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, CommunityPostsRepository communityPostsRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.communityPostsRepository = communityPostsRepository;
        this.userRepository = userRepository;
    }

    // 댓글 추가
    public CommentResponse addComment(Long postId, Long userId, CommentRequest request) {
        CommunityPosts post = communityPostsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comments comment = new Comments();
        comment.setPost(post);
        comment.setUser(user);
        comment.setComment_text(request.getContent());

        Comments savedComment = commentRepository.save(comment);
        return new CommentResponse("success", savedComment.getComment_id());
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentRepository.delete(comment);
    }

    // 댓글 비공개 설정 메서드
    public Comments setCommentVisibility(Long commentId, Boolean isSecret) {
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setIsSecret(isSecret);
        return commentRepository.save(comment);
    }
}
