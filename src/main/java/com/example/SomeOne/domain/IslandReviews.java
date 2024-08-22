package com.example.SomeOne.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IslandReviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "island_id")
    private Island island;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    private int rating; // 별점
    private String shortReview; // 한줄평
    private String detailedReview; // 상세 리뷰

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_record_id")
    private TravelRecords travelRecord; // TravelRecords와 연결된 리뷰

    public void setTravelRecord(TravelRecords travelRecord) {
        this.travelRecord = travelRecord;
    }

    @Builder
    public IslandReviews(Island island, Users user, int rating, String shortReview, String detailedReview) {
        this.island = island;
        this.user = user;
        this.rating = rating;
        this.shortReview = shortReview;
        this.detailedReview = detailedReview;
    }
}
