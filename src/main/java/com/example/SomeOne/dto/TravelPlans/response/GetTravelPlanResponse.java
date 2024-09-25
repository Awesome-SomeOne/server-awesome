package com.example.SomeOne.dto.TravelPlans.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTravelPlanResponse {
    private String planName;
    private String islandName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double temperature;
    private List<TravelPlaceResponse> travelPlaceList;
}