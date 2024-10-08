package com.example.SomeOne.service;

import com.example.SomeOne.domain.BusinessReviews;
import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.enums.Business_category;
import com.example.SomeOne.dto.Businesses.response.GetLandmarkResponse;
import com.example.SomeOne.dto.Businesses.response.PopularityPlaceResponse;
import com.example.SomeOne.dto.Businesses.response.ReviewResponse;
import com.example.SomeOne.repository.BusinessReviewsRepository;
import com.example.SomeOne.repository.BusinessesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopularityService {

    private final BusinessesRepository businessesRepository;
    private final BusinessReviewsRepository businessReviewsRepository;
    private final FavoritesService favoritesService;

    public List<PopularityPlaceResponse> listLandmark(Long userId, Long islandId) {
        List<Businesses> businessesList = businessesRepository.
                findByIslandIdAndBusinessTypeOrderByRatingDesc(islandId, Business_category.관광지);

        return businessesList.stream()
                .map(business -> {
                    Double averageRating = businessesRepository.findAverageRatingByBusinessId(business.getBusiness_id());
                    boolean status = favoritesService.findFavorite(userId, business.getBusiness_id());
                    return new PopularityPlaceResponse(
                            business.getBusiness_id(),
                            business.getBusiness_name(),
                            business.getAddress(),
                            business.getBusinessType(),
                            business.getImg_url(),
                            averageRating != null ? averageRating : 0.0,
                            status
                    );
                })
                .collect(Collectors.toList());
    }

    public List<PopularityPlaceResponse> recommendPlaceList(Long userId, Long islandId, Business_category category) {
        List<Businesses> businessesList = businessesRepository.
                findByIslandIdAndBusinessTypeOrderByRatingDesc(islandId, category);

        return businessesList.stream()
                .map(business -> {
                    Double averageRating = businessesRepository.findAverageRatingByBusinessId(business.getBusiness_id());
                    boolean status = favoritesService.findFavorite(userId, business.getBusiness_id());
                    return new PopularityPlaceResponse(
                            business.getBusiness_id(),
                            business.getBusiness_name(),
                            business.getAddress(),
                            business.getBusinessType(),
                            business.getImg_url(),
                            averageRating != null ? averageRating : 0.0,
                            status
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public GetLandmarkResponse getPlace(Long userId, Long businessId) {
        Businesses businesses = businessesRepository.findById(businessId).orElseThrow(() -> new IllegalArgumentException());
        List<BusinessReviews> reviews = businessReviewsRepository.findAllByBusinessId(businessId);

        boolean status = favoritesService.findFavorite(userId, businessId);

        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(review -> new ReviewResponse(
                        review.getUser().getUsername(),
                        review.getRating(),
                        review.getBusinessReview()
                ))
                .collect(Collectors.toList());

        // LandmarkResponse DTO 생성 및 반환
        return new GetLandmarkResponse(
                businesses.getBusiness_id(),
                businesses.getBusiness_name(),
                businesses.getAddress(),
                businesses.getX_address(),
                businesses.getY_address(),
                businesses.getImg_url(),
                status,
                reviewResponses
        );
    }
}