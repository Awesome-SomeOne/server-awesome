package com.example.SomeOne.service;

import com.example.SomeOne.domain.*;
import com.example.SomeOne.dto.TravelPlans.request.TravelPlanRequest;
import com.example.SomeOne.dto.TravelPlans.response.GetTravelPlanResponse;
import com.example.SomeOne.dto.TravelPlans.response.GetPlansResponse;
import com.example.SomeOne.dto.TravelPlans.response.TravelPlaceResponse;
import com.example.SomeOne.repository.TravelPlansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPlansService {

    private final IslandService islandService;
    private final TravelPlansRepository travelPlansRepository;
    private final TravelPlaceService travelPlaceService;

    @Transactional
    public void save(TravelPlanRequest request) {
        Island island = islandService.findById(request.getIslandId());

        TravelPlans travelPlan = new TravelPlans(new Users(), request.getPlanName(), request.getStartDate(),
                request.getEndDate(), island);

        travelPlansRepository.save(travelPlan);
    }

    public List<GetPlansResponse> getPlan(Long userId) {
        List<TravelPlans> planList = travelPlansRepository.findByUser_IdOrderByStart_dateDesc(userId);

        return planList.stream().map(p -> new GetPlansResponse(p.getPlan_id(), p.getPlan_name(), p.getIsland().getAddress(),
                p.getStart_date(), p.getEnd_date(), p.getStatus())).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long planId) {
        TravelPlans plan = travelPlansRepository.findById(planId).orElseThrow(() -> new IllegalArgumentException());
        travelPlansRepository.delete(plan);
    }

    public TravelPlans findById(Long id) {
        return travelPlansRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
    }

    public GetTravelPlanResponse findTravelPlan(Long planId) {
        TravelPlans travelPlans = findById(planId);
        String planName = travelPlans.getPlan_name();
        String islandName = travelPlans.getIsland().getIsland_name();
        List<TravelPlace> travelPlaceList = travelPlaceService.findByTravelPlan(planId);

        List<TravelPlaceResponse> responseList = travelPlaceList.stream().map((p -> new TravelPlaceResponse(
                p.getPlace_id(), p.getBusinesses().getBusiness_name(),
                p.getBusinesses().getAddress(), p.getBusinesses().getBusiness_type(), p.getDate(), p.getOrder(),
                p.getBusinesses().getImg_url()))).collect(Collectors.toList());

        return new GetTravelPlanResponse(planName, islandName, responseList);
    }
}
