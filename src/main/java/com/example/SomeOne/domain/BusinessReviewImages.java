package com.example.SomeOne.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BusinessReviewImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private BusinessReviews review;

    private String imageUrl;

    public BusinessReviewImages(BusinessReviews review, String imageUrl) {
        this.review = review;
        this.imageUrl = imageUrl;
    }
}