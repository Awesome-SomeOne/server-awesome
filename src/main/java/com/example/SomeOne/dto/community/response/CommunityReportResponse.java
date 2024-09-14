package com.example.SomeOne.dto.community.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityReportResponse {
    private String status;
    private String message;

    public CommunityReportResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
