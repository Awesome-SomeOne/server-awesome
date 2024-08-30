package com.example.SomeOne.domain;

import com.example.SomeOne.domain.enums.ReportReason;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_record_id")
    private TravelRecords travelRecord;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    private String customReason;

    public Report(TravelRecords travelRecord, ReportReason reason, String customReason) {
        this.travelRecord = travelRecord;
        this.reason = reason;
        this.customReason = customReason;
    }
}
