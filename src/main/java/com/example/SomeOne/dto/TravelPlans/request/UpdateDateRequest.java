package com.example.SomeOne.dto.TravelPlans.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDateRequest {
    private Long travelPlaceId;
    private Long travelPlanId;
    private Long businessId;
    private LocalDate date;
}
