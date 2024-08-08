package com.example.SomeOne.domain;

import com.example.SomeOne.domain.enums.Business_category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Businesses {

    @Id @GeneratedValue
    private Long business_id;

    private String business_name;
    private Business_category business_type;
    private String address;
    private String operating_hours;
    private String closed_days;
    private String phone_number;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "island_id")
    private Island island;
}
