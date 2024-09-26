package com.example.SomeOne.domain;

import com.example.SomeOne.domain.enums.ReportReason;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BusinessReviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Businesses business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(nullable = false)
    private Integer rating;

    private String businessReview;

    // 비즈니스 리뷰 신고 상태
    private Boolean isReported = false; // 기본값은 신고되지 않음

    // 리뷰 공개 여부 (공개 여부는 필요에 따라 설정)
    private Boolean publicPrivate = true; // 기본값은 공개

    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;

    // TravelRecords와의 연관 관계 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_record_id")
    @JsonBackReference
    private TravelRecords travelRecord;

    @Builder
    public BusinessReviews(Businesses business, Users user, Integer rating, String businessReview, TravelRecords travelRecord) {
        this.business = business;
        this.user = user;
        this.rating = rating;
        this.businessReview = businessReview;
        this.travelRecord = travelRecord;
    }

    // 양방향 연관 관계 설정
    public void setTravelRecord(TravelRecords travelRecord) {
        if (this.travelRecord != travelRecord) {
            if (this.travelRecord != null) {
                this.travelRecord.getBusinessReviews().remove(this);
            }
            this.travelRecord = travelRecord;
            if (travelRecord != null) {
                travelRecord.getBusinessReviews().add(this);
            }
        }
    }

    // 신고 처리 메서드
    public void report(ReportReason reason) {
        this.isReported = true;
        this.reportReason = reason;
    }

    // 신고로 비공개 처리 메서드
    public void hideRecordDueToReport() {
        this.publicPrivate = false; // 신고로 인해 비공개 처리
        this.isReported = true;     // 신고 상태 업데이트
    }
}