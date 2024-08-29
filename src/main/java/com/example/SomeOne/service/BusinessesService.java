package com.example.SomeOne.service;

import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.enums.Business_category;
import com.example.SomeOne.dto.Businesses.response.FamousPlaceResponse;
import com.example.SomeOne.dto.Businesses.response.FindBusinessesResponse;
import com.example.SomeOne.dto.Businesses.response.RecommendPlaceResponse;
import com.example.SomeOne.repository.BusinessesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessesService {

    private final BusinessesRepository businessesRepository;

    public List<FindBusinessesResponse> findBusinesses(String keyword) {
        List<Businesses> businessesList = businessesRepository.findByKeyword(keyword);

        return businessesList.stream()
                .map(business -> new FindBusinessesResponse(business.getBusiness_id(), business.getBusiness_name(), business.getAddress(),
                        business.getBusinessType())).collect(Collectors.toList());
    }

    public List<RecommendPlaceResponse> recommendPlace(Long islandId, String category) {
        List<Businesses> businessesList = businessesRepository.findByIslandIdAndBusinessType(islandId,
                Business_category.valueOf(category));

        return businessesList.stream()
                .map(business -> new RecommendPlaceResponse(business.getBusiness_id(), business.getBusiness_name(), business.getAddress(),
                        business.getBusinessType())).collect(Collectors.toList());
    }

    public Businesses findById(Long id) {
        return businessesRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
    }

    public List<FamousPlaceResponse> famousPlace(Long id) {
        List<Businesses> businessesList = businessesRepository.findByIslandId(id);
        Collections.shuffle(businessesList);

        List<Businesses> fiveList = businessesList.stream().limit(5).collect(Collectors.toList());

        return fiveList.stream().map(b -> new FamousPlaceResponse(b.getBusiness_id(), b.getBusiness_name(), b.getBusinessType(),
                b.getAddress(), b.getImg_url())).collect(Collectors.toList());
    }
}
