package com.example.SomeOne.dto.community.request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityReportRequest {
    private String reason;
    private String details;

    public String getReason() {
        return reason;
    }

    public String getDetails() {
        return details;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
