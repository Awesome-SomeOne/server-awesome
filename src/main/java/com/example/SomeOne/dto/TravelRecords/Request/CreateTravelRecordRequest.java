package com.example.SomeOne.dto.TravelRecords.Request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTravelRecordRequest {

    private Long planId;

    @Size(max = 25)
    private String oneLineReview;

    @Size(max = 500)
    private String overallReview;

    private boolean publicPrivate;
}
