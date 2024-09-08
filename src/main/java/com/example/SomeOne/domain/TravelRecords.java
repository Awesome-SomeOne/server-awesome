package com.example.SomeOne.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;  // 필드명을 CamelCase로 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private TravelPlans plan;

    private String recordTitle;
    private String recordContent;
    private Boolean publicPrivate;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordImages> recordImages = new ArrayList<>();

    @OneToMany(mappedBy = "travelRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    private Boolean isReported = false; // 신고 상태

    @OneToOne(mappedBy = "travelRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private IslandReviews islandReview;  // TravelRecords와 연결된 섬 리뷰

    // 기록 생성 시 리뷰 추가
    public void addIslandReview(IslandReviews islandReview) {
        this.islandReview = islandReview;
        islandReview.setTravelRecord(this);
    }

    @Builder
    public TravelRecords(Users user, TravelPlans plan, String recordTitle, String recordContent, Boolean publicPrivate) {
        this.user = user;
        this.plan = plan;
        this.recordTitle = recordTitle;
        this.recordContent = recordContent;
        this.publicPrivate = publicPrivate;
    }

    public void addRecordImage(RecordImages recordImage) {
        this.recordImages.add(recordImage);
        recordImage.setRecord(this);
    }

    public void addReport(Report report) {
        this.reports.add(report);
        report.setTravelRecord(this);
    }

    // 신고 처리 메서드
    public void hideRecordDueToReport() {
        this.publicPrivate = false;
        this.isReported = true; // 신고 상태 업데이트
    }

    // 생성 시 기본 공개 상태 설정
    public static TravelRecords createPublicRecord(Users user, TravelPlans plan, String title, String content) {
        return new TravelRecords(user, plan, title, content, true);
    }
}
