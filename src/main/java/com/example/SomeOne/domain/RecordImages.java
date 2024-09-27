package com.example.SomeOne.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private TravelRecords record;

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