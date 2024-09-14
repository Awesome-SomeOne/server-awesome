package com.example.SomeOne.dto.community.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommunityPostResponse {
    private Long postId;
    private String status;
    private List<String> imageUrls;
    private Long userId;

    public CommunityPostResponse(String status, Long postId, List<String> imageUrls, Long userId) {
        this.status = status;
        this.postId = postId;
        this.imageUrls = imageUrls;
        this.userId = userId;
    }
}
