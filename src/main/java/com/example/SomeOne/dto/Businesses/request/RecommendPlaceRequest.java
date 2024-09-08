package com.example.SomeOne.dto.Businesses.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendPlaceRequest {
    private Long islandId;
    private String category;
}
