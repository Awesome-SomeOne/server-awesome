package com.example.SomeOne.dto.Businesses.response;

import com.example.SomeOne.domain.enums.Business_category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendPlaceResponse {
    private Long id;
    private String name;
    private String address;
    private String x_address;
    private String y_address;
    private Business_category category;
}
