package com.example.SomeOne.dto.TravelRecords.Request;

import com.example.SomeOne.domain.enums.ReportReason;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {

    private Long travelRecordId;
    private ReportReason reason;
    private String additionalInfo;

    public Long getTravelRecordId() {
        return travelRecordId;
    }

    public void setTravelRecordId(Long travelRecordId) {
        this.travelRecordId = travelRecordId;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}