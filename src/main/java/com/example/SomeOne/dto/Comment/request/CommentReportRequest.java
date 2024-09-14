package com.example.SomeOne.dto.Comment.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReportRequest {
    private String reason;
    private String details;
}
