package com.example.SomeOne.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Island {

    @Id @GeneratedValue
    private Long island_id;

    private String island_name;
    private String address;
}
