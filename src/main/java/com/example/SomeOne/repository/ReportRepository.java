package com.example.SomeOne.repository;

import com.example.SomeOne.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
