package com.example.SomeOne.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private TravelRecords record;

    // Other fields and methods

    public void setRecord(TravelRecords record) {
        if (this.record != null) {
            this.record.getRecordImages().remove(this);
        }
        this.record = record;
        if (record != null) {
            record.getRecordImages().add(this);
        }
    }

}