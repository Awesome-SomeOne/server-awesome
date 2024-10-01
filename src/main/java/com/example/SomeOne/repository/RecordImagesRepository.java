package com.example.SomeOne.repository;

import com.example.SomeOne.domain.RecordImages;
import com.example.SomeOne.domain.TravelRecords;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordImagesRepository extends JpaRepository<RecordImages, Long> {
    void deleteAllByRecord(TravelRecords record);
}