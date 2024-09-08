package com.example.SomeOne.dto.Businesses.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetLandmarkResponse {
    private Long businessId;
    private String businessName;
    private String address;
    private String xAddress;
    private String yAddress;
    private String imgUrl;
    private List<ReviewResponse> reviews;
}
