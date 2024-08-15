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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private TravelPlans travelPlans;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Businesses businesses;

    private LocalDate date;

    private Integer placeOrder;
    @Builder
    public TravelPlace(TravelPlans travelPlans, Businesses businesses, LocalDate date, Integer placeOrder) {
        this.travelPlans = travelPlans;
        this.businesses = businesses;
        this.date = date;
        this.placeOrder = placeOrder;
    }

    public void plusOrder() {
        this.placeOrder += 1;
    }

    public void minusOrder() {
        this.placeOrder -= 1;
    }

    public void changeOrder(Integer changeOrder) {
        this.placeOrder = changeOrder;
    }
}
