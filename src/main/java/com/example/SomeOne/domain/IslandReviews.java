package com.example.SomeOne.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.*;
@Getter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"island_id", "users_id"})
        }
)
@Entity
public class IslandReviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "island_id")
    private Island island;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users user;

    @Column(nullable = false)
    @Max(5)
    private Integer rating;

    private String shortReview;
    private String detailedReview;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_record_id")
    @JsonIgnore
    private TravelRecords travelRecord;

    public IslandReviews() {} // 기본 생성자 추가

    public void setTravelRecord(TravelRecords travelRecord) {
        this.travelRecord = travelRecord;
    }

    @Builder
    public IslandReviews(Island island, Users user, Integer rating, String shortReview, String detailedReview) {
        this.island = island;
        this.user = user;
        this.rating = rating;
        this.shortReview = shortReview;
        this.detailedReview = detailedReview;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setShortReview(String shortReview) {
        this.shortReview = shortReview;
    }

    public void setDetailedReview(String detailedReview) {
        this.detailedReview = detailedReview;
    }
}