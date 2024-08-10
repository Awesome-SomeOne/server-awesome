package com.example.SomeOne.service;

import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.Island;
import com.example.SomeOne.domain.TravelPlans;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.domain.enums.Business_category;
import com.example.SomeOne.dto.TravelPlans.request.TravelPlanRequest;
import com.example.SomeOne.dto.TravelPlans.response.GetPlanResponse;
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

    @Transactional
    public void save(TravelPlanRequest request) {
        Island island = islandService.findById(request.getIslandId());

        TravelPlans travelPlan = new TravelPlans(new Users(), request.getPlanName(), request.getStartDate(),
                request.getEndDate(), island);

        travelPlansRepository.save(travelPlan);
    }

    public List<GetPlanResponse> getPlan(Long userId) {
        List<TravelPlans> planList = travelPlansRepository.findByUser_IdOrderByStart_dateDesc(userId);

        return planList.stream().map(p -> new GetPlanResponse(p.getPlan_id(), p.getPlan_name(), p.getIsland().getAddress(),
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
}
