package com.example.SomeOne.dto.TravelPlans.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeOrderRequest {
    private Long travelPlaceId;
    private Integer order;
}
