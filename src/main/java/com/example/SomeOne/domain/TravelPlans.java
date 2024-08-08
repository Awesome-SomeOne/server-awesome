package com.example.SomeOne.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class TravelPlans {

    @Id @GeneratedValue
    private Long plan_id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "users_id")
    private Users user;
    private String plan_name;
    private LocalDate start_date;
    private LocalDate end_date;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "island_id")
    private Island island;

    @Builder
    public TravelPlans(Users user, String plan_name, LocalDate start_date, LocalDate end_date, Island island) {
        this.user = user;
        this.plan_name = plan_name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.island = island;
    }
}
