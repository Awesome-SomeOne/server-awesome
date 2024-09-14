package com.example.SomeOne.dto.Bookmark.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkResponse {
    private String status;
    private String message;

    public BookmarkResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}