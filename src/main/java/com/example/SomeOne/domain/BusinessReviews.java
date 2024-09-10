package com.example.SomeOne.domain;

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


    @Builder
    public BusinessReviews(Businesses business, Users user, Integer rating, String businessReview) {
        this.business = business;
        this.user = user;
        this.rating = rating;
        this.businessReview = businessReview;

    }
}
