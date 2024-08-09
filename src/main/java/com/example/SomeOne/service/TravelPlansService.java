package com.example.SomeOne.service;

import com.example.SomeOne.domain.Island;
import com.example.SomeOne.domain.TravelPlans;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.dto.TravelPlans.request.TravelPlanRequest;
import com.example.SomeOne.repository.TravelPlansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
