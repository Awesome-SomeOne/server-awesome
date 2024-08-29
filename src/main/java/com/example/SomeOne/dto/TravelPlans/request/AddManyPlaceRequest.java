package com.example.SomeOne.dto.TravelPlans.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddManyPlaceRequest {
    private Long travelPlanId;
    private List<Long> businessIds;
    private LocalDate date;
}
