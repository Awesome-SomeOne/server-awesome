package com.example.SomeOne.controller;

import com.example.SomeOne.dto.Comment.request.CommentReportRequest;
import com.example.SomeOne.dto.Comment.request.CommentRequest;
import com.example.SomeOne.dto.Comment.request.CommentVisibilityRequest;
import com.example.SomeOne.dto.Comment.request.LikeRequest;
import com.example.SomeOne.dto.Comment.response.CommentResponse;
import com.example.SomeOne.service.CommentLikeService;
import com.example.SomeOne.service.CommentReportService;
import com.example.SomeOne.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final CommentReportService commentReportService;

    @Autowired
    public CommentController(CommentService commentService, CommentLikeService commentLikeService, CommentReportService commentReportService) {

        this.commentService = commentService;
        this.commentLikeService = commentLikeService;
        this.commentReportService = commentReportService;
    }

    // 댓글 추가
    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long postId,
                                                      @RequestParam Long userId,
                                                      @RequestBody CommentRequest request) {
        CommentResponse response = commentService.addComment(postId, userId, request);
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 비공개 여부 수정
    @PatchMapping("/{commentId}/visibility")
    public ResponseEntity<String> updateCommentVisibility(
            @PathVariable Long commentId,
            @RequestBody CommentVisibilityRequest visibilityRequest) {

        commentService.setCommentVisibility(commentId, visibilityRequest.getIsSecret());
        return ResponseEntity.ok("{\"status_code\": 200, \"message\": \"댓글 비밀 공개 설정이 성공적으로 변경되었습니다.\"}");
    }

    // 댓글에 좋아요 추가
    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> addLike(@PathVariable Long commentId, @RequestBody LikeRequest likeRequest) {
        String result = commentLikeService.addLike(commentId, likeRequest.getUserId());
        return ResponseEntity.ok(result);
    }

    // 댓글에 좋아요 취소
    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<String> removeLike(@PathVariable Long commentId, @RequestBody LikeRequest likeRequest) {
        String result = commentLikeService.removeLike(commentId, likeRequest.getUserId());
        return ResponseEntity.ok(result);
    }

    // 댓글 신고
    @PostMapping("/{commentId}/report")
    public ResponseEntity<String> reportComment(
            @PathVariable Long commentId,
            @RequestBody CommentReportRequest reportRequest,
            @RequestParam Long userId) {

        String result = commentReportService.reportComment(commentId, userId, reportRequest.getReason(), reportRequest.getDetails());
        return ResponseEntity.ok("{\"status\": \"success\", \"message\": \"" + result + "\"}");
    }
}
