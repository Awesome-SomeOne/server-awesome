package com.example.SomeOne.dto.TravelPlans.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlanRequest {
    private String islandName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String planName;
}
