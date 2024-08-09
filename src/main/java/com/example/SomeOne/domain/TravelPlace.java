package com.example.SomeOne.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class TravelPlace {

    @Id
    @GeneratedValue
    private Long place_id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "plan_id")
    private TravelPlans travelPlans;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "business_id")
    private Businesses businesses;

    private LocalDate date;

    @Builder
    public TravelPlace(TravelPlans travelPlans, Businesses businesses, LocalDate date) {
        this.travelPlans = travelPlans;
        this.businesses = businesses;
        this.date = date;
    }
}
