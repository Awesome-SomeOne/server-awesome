package com.example.SomeOne.dto.Businesses.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private String user;
    private Integer rating;
    private String content;
}
