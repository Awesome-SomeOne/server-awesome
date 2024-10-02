package com.example.SomeOne.service;

import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.TravelPlace;
import com.example.SomeOne.domain.TravelPlans;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.dto.TravelPlans.request.UpdatePlaceRequest;
import com.example.SomeOne.dto.TravelPlans.response.AddManyPlaceResponse;
import com.example.SomeOne.dto.TravelPlans.response.AddPlaceResponse;
import com.example.SomeOne.repository.TravelPlaceRepository;
import com.example.SomeOne.repository.TravelPlansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPlaceService {

    private final TravelPlaceRepository travelPlaceRepository;
    private final TravelPlansRepository travelPlansRepository;
    private final BusinessesService businessesService;
    private final UserService userService;

    @Transactional
    public AddPlaceResponse addPlace(Long userId, Long travelPlanId, Long businessId, LocalDate date) {
        TravelPlans travelPlan = travelPlansRepository.findById(travelPlanId).orElseThrow(
                () -> new IllegalArgumentException());
        Businesses business = businessesService.findById(businessId);

        List<TravelPlace> list = travelPlaceRepository.findAllByTravelPlans_PlanIdAndDate(travelPlanId, date);
        int size = list.size();

        TravelPlace travelPlace = TravelPlace.builder().travelPlans(travelPlan).businesses(business)
                .date(date).placeOrder(size + 1).build();

        TravelPlace place = travelPlaceRepository.save(travelPlace);

        AddPlaceResponse response = new AddPlaceResponse(place.getPlace_id());
        return response;
    }

    @Transactional
    public AddManyPlaceResponse addManyPlaces(Long userId, Long travelPlanId, List<Long> businessIds, LocalDate date) {
        List<AddPlaceResponse> placeResponses = new ArrayList<>();

        for (Long businessId : businessIds) {
            AddPlaceResponse response = addPlace(userId, travelPlanId, businessId, date);
            placeResponses.add(response);
        }

        return new AddManyPlaceResponse(placeResponses);
    }

    @Transactional
    public void deletePlace(Long userId, Long travelPlaceId) {
        TravelPlace travelPlace = findById(travelPlaceId);
        Long travelPlanId = travelPlace.getTravelPlans().getPlanId();

        Long planUser = travelPlace.getTravelPlans().getUser().getUsers_id();
        if (planUser != userId) {
            throw new IllegalArgumentException("Wrong user");
        }

        LocalDate date = travelPlace.getDate();
        List<TravelPlace> placeList = travelPlaceRepository.
                findAllByTravelPlans_PlanIdAndDateOrderByPlaceOrderAsc(travelPlanId, date);

        Integer order = travelPlace.getPlaceOrder();

        for (int i = 0; i < placeList.size(); i++) {
            if (placeList.get(i).getPlaceOrder() > order) {
                placeList.get(i).minusOrder();
            }
        }

        travelPlace.getTravelPlans().getTravelPlaces().remove(travelPlace);

        travelPlaceRepository.delete(travelPlace);
    }

    @Transactional
    public void updatePlace(Long userId, List<UpdatePlaceRequest> request) {
        for (int i = 0; i < request.size(); i++) {
            Long travelPlaceId = request.get(i).getTravelPlaceId();
            TravelPlace travelPlace = findById(travelPlaceId);
            travelPlace.update(request.get(i).getOrder(), request.get(i).getDate());
        }
    }

    @Transactional
    public void updateDate(Long userId, Long travelPlaceId, Long travelPlanId, Long businessId, LocalDate date) {
        deletePlace(userId, travelPlaceId);
        addPlace(userId, travelPlanId, businessId, date);
    }

    @Transactional
    public void changeOrder(Long userId, Long travelPlaceId, Integer changeOrder) {
        TravelPlace travelPlace = findById(travelPlaceId);
        Long travelPlanId = travelPlace.getTravelPlans().getPlanId();

        LocalDate date = travelPlace.getDate();
        List<TravelPlace> placeList = travelPlaceRepository.
                findAllByTravelPlans_PlanIdAndDateOrderByPlaceOrderAsc(travelPlanId, date);

        Integer currentOrder = travelPlace.getPlaceOrder();
        if (changeOrder < currentOrder) {
            // changeOrder가 currentOrder보다 작은 경우, 중간의 모든 순서를 1씩 증가시킴
            for (TravelPlace place : placeList) {
                if (place.getPlaceOrder() >= changeOrder && place.getPlaceOrder() < currentOrder) {
                    place.plusOrder();
                }
            }
        } else if (changeOrder > currentOrder) {
            // changeOrder가 currentOrder보다 큰 경우, 중간의 모든 순서를 1씩 감소시킴
            for (TravelPlace place : placeList) {
                if (place.getPlaceOrder() > currentOrder && place.getPlaceOrder() <= changeOrder) {
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

    public List<TravelPlace> findByTravelPlan(Long userId, Long planId) {
        Users user = userService.findById(userId);
        return travelPlaceRepository.findAllByTravelPlans_PlanIdAndUserOrderByDateAsc(planId, user);
    }
}
