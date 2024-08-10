package com.example.SomeOne.service;

import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.TravelPlace;
import com.example.SomeOne.domain.TravelPlans;
import com.example.SomeOne.repository.TravelPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPlaceService {

    private final TravelPlaceRepository travelPlaceRepository;
    private final TravelPlansService travelPlansService;
    private final BusinessesService businessesService;

    @Transactional
    public void addPlace(Long travelPlanId, Long businessId, LocalDate date) {
        TravelPlans travelPlan = travelPlansService.findById(travelPlanId);
        Businesses business = businessesService.findById(businessId);

        List<TravelPlace> list = travelPlaceRepository.findAllByTravelPlans_PlanIdAndDate(travelPlanId, date);
        int size = list.size();

        TravelPlace travelPlace = TravelPlace.builder().travelPlans(travelPlan).businesses(business)
                .date(date).order(size + 1).build();

        travelPlaceRepository.save(travelPlace);
    }

    @Transactional
    public void deletePlace(Long travelPlaceId) {
        TravelPlace travelPlace = travelPlaceRepository.findById(travelPlaceId).orElseThrow(() -> new IllegalArgumentException());
        Long travelPlanId = travelPlace.getTravelPlans().getPlan_id();

        LocalDate date = travelPlace.getDate();
        List<TravelPlace> placeList = travelPlaceRepository.
                findAllByTravelPlans_PlanIdAndDateOrderByOrderAsc(travelPlanId, date);

        Integer order = travelPlace.getOrder();

        for (int i = 0; i < placeList.size(); i++) {
            if (placeList.get(i).getOrder() > order) {
                placeList.get(i).minusOrder();
            }
        }

        travelPlaceRepository.delete(travelPlace);
    }

    @Transactional
    public void updateDate(Long travelPlaceId, Long travelPlanId, Long businessId, LocalDate date) {
        deletePlace(travelPlaceId);
        addPlace(travelPlanId, businessId, date);
    }
}
