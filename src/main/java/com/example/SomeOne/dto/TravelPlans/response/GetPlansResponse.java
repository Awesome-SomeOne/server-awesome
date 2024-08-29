package com.example.SomeOne.dto.TravelPlans.response;

import com.example.SomeOne.domain.enums.TravelStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPlansResponse {
    private Long planId;
    private String name;
    private String address;
    private LocalDate start_date;
    private LocalDate end_date;
    private TravelStatus status;
}
