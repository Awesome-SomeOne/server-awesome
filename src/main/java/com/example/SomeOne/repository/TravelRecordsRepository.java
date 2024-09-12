package com.example.SomeOne.repository;

import com.example.SomeOne.domain.TravelPlans;
import com.example.SomeOne.domain.TravelRecords;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TravelRecordsRepository extends JpaRepository<TravelRecords, Long> {

    @Query("SELECT tr FROM TravelRecords tr JOIN FETCH tr.recordImages WHERE tr.user = :user ORDER BY tr.recordId DESC")
    List<TravelRecords> findByUserWithImagesOrderByRecordIdDesc(@Param("user") Users user);

    @Query("SELECT tr FROM TravelRecords tr JOIN FETCH tr.recordImages WHERE tr.plan = :plan ORDER BY tr.recordId DESC")
    List<TravelRecords> findByPlanWithImagesOrderByRecordIdDesc(@Param("plan") TravelPlans plan);

    List<TravelRecords> findByUserAndPublicPrivateOrderByRecordIdDesc(Users user, Boolean publicPrivate);

    List<TravelRecords> findByPlanAndPublicPrivateOrderByRecordIdDesc(TravelPlans plan, Boolean publicPrivate);


}