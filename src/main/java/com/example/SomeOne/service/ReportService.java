package com.example.SomeOne.service;

import com.example.SomeOne.domain.Report;
import com.example.SomeOne.domain.TravelRecords;
import com.example.SomeOne.domain.enums.ReportReason;
import com.example.SomeOne.repository.ReportRepository;
import com.example.SomeOne.repository.TravelRecordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final TravelRecordsRepository travelRecordsRepository;

    @Transactional
    public void createReport(Long travelRecordId, ReportReason reason, String additionalInfo) {
        TravelRecords travelRecord = travelRecordsRepository.findById(travelRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found with id: " + travelRecordId));

        // 신고 생성
        Report report = new Report(travelRecord, reason, additionalInfo);
        reportRepository.save(report);

        // 신고로 인한 숨김 처리
        travelRecord.hideRecordDueToReport();
        travelRecordsRepository.save(travelRecord);
    }
}
