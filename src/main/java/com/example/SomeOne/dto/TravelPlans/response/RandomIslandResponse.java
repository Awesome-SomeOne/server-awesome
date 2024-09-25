package com.example.SomeOne.dto.TravelPlans.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RandomIslandResponse {
    private Long id;
    private String islandName;
    private String address;
    private String img_url;
}
