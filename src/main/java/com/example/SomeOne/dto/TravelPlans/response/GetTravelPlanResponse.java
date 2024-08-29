package com.example.SomeOne.dto.TravelPlans.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTravelPlanResponse {
    private String planName;
    private String islandName;
    private List<TravelPlaceResponse> travelPlaceList;
}