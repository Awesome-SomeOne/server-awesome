package com.example.SomeOne.dto.TravelRecords;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TravelDateImages {
    private LocalDate travelDate;
    private List<String> imageUrls;
}