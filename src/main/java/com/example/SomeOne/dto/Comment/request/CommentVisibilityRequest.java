package com.example.SomeOne.dto.Comment.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentVisibilityRequest {
    private Boolean isSecret;
}