package com.example.SomeOne.dto.TravelPlans.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindIslandResponse {
    private Long id;
    private String name;
    private String address;
}
