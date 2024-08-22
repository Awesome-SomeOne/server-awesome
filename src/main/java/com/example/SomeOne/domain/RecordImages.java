package com.example.SomeOne.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordImages {

    @Id @GeneratedValue
    private Long image_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private TravelRecords record;

    private String image_url;

    @Builder
    public RecordImages(String image_url) {
        this.image_url = image_url;
    }

    public void setRecord(TravelRecords record) {
        this.record = record;
    }
}