package com.example.SomeOne.dto.Comment.response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse {
    private String status;
    private Long commentId;

    public CommentResponse(String status, Long commentId) {
        this.status = status;
        this.commentId = commentId;
    }
}