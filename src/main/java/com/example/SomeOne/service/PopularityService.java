package com.example.SomeOne.service;

import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.enums.Business_category;
import com.example.SomeOne.dto.Businesses.response.PopularityPlaceResponse;
import com.example.SomeOne.dto.Businesses.response.RecommendPlaceResponse;
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

    public List<PopularityPlaceResponse> listLandmark(Long islandId) {
        List<Businesses> businessesList = businessesRepository.
                findByIslandIdAndBusinessTypeOrderByRatingDesc(islandId, Business_category.관광지);

        return businessesList.stream()
                .map(business -> {
                    Double averageRating = businessesRepository.findAverageRatingByBusinessId(business.getBusiness_id());
                    return new PopularityPlaceResponse(
                            business.getBusiness_id(),
                            business.getBusiness_name(),
                            business.getAddress(),
                            business.getBusinessType(),
                            averageRating != null ? averageRating : 0.0
                    );
                })
                .collect(Collectors.toList());
    }

    public List<PopularityPlaceResponse> recommendPlaceList(Long islandId, Business_category category) {
        List<Businesses> businessesList = businessesRepository.
                findByIslandIdAndBusinessTypeOrderByRatingDesc(islandId, category);

        return businessesList.stream()
                .map(business -> {
                    Double averageRating = businessesRepository.findAverageRatingByBusinessId(business.getBusiness_id());
                    return new PopularityPlaceResponse(
                            business.getBusiness_id(),
                            business.getBusiness_name(),
                            business.getAddress(),
                            business.getBusinessType(),
                            averageRating != null ? averageRating : 0.0
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void findOnePlace(Long businessId) {
        Businesses businesses = businessesRepository.findById(businessId).orElseThrow(() -> new IllegalArgumentException());


    }
}
