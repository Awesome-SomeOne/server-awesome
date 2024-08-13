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
        TravelPlace travelPlace = findById(travelPlaceId);
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

    @Transactional
    public void changeOrder(Long travelPlaceId, Integer changeOrder) {
        TravelPlace travelPlace = findById(travelPlaceId);
        Long travelPlanId = travelPlace.getTravelPlans().getPlan_id();

        LocalDate date = travelPlace.getDate();
        List<TravelPlace> placeList = travelPlaceRepository.
                findAllByTravelPlans_PlanIdAndDateOrderByOrderAsc(travelPlanId, date);

        Integer currentOrder = travelPlace.getOrder();
        if (changeOrder < currentOrder) {
            // changeOrder가 currentOrder보다 작은 경우, 중간의 모든 순서를 1씩 증가시킴
            for (TravelPlace place : placeList) {
                if (place.getOrder() >= changeOrder && place.getOrder() < currentOrder) {
                    place.plusOrder();
                }
            }
        } else if (changeOrder > currentOrder) {
            // changeOrder가 currentOrder보다 큰 경우, 중간의 모든 순서를 1씩 감소시킴
            for (TravelPlace place : placeList) {
                if (place.getOrder() > currentOrder && place.getOrder() <= changeOrder) {
                    place.minusOrder();
                }
            }
        }

        travelPlace.changeOrder(changeOrder);

        travelPlaceRepository.save(travelPlace);
    }

    public TravelPlace findById(Long travelPlaceId) {
        return travelPlaceRepository.findById(travelPlaceId).orElseThrow(() -> new IllegalArgumentException());
    }

    public List<TravelPlace> findByTravelPlan(Long planId) {
        return travelPlaceRepository.findAllByTravelPlans_PlanIdOrderByDateAsc(planId);
    }
}
