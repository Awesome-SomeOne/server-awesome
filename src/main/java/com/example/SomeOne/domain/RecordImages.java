package com.example.SomeOne.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RecordImages {

    @Id @GeneratedValue
    private Long image_id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "record_id")
    private TravelRecords record;

    private String image_url;
}
