package com.example.SomeOne.controller;

import com.example.SomeOne.dto.TravelRecords.Request.ReportRequest;
import com.example.SomeOne.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/travel-records")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 신고 생성
    @PostMapping("/report")
    public ResponseEntity<String> createReport(@RequestBody ReportRequest reportRequest) {
        try {
            reportService.createReport(
                    reportRequest.getTravelRecordId(),
                    reportRequest.getReason(),
                    reportRequest.getAdditionalInfo()
            );
            return new ResponseEntity<>("Report created and travel record hidden successfully.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}