package com.example.SomeOne.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class TravelPlace {

    @Id
    @GeneratedValue
    private Long plan_id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "plan_id")
    private TravelPlans travelPlans;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "business_id")
    private Businesses businesses;

    private LocalDate date;
}
