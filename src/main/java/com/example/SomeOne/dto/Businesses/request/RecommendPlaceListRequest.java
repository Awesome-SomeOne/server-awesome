package com.example.SomeOne.dto.Businesses.request;

import com.example.SomeOne.domain.enums.Business_category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendPlaceListRequest {
    private Long islandId;
    private Business_category category;
}
