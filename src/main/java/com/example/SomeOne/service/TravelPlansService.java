package com.example.SomeOne.service;

import com.example.SomeOne.domain.*;
import com.example.SomeOne.dto.TravelPlans.request.TravelPlanRequest;
import com.example.SomeOne.dto.TravelPlans.response.GetTravelPlanResponse;
import com.example.SomeOne.dto.TravelPlans.response.GetPlansResponse;
import com.example.SomeOne.dto.TravelPlans.response.SaveTravelResponse;
import com.example.SomeOne.dto.TravelPlans.response.TravelPlaceResponse;
import com.example.SomeOne.dto.weather.WeatherNowDTO;
import com.example.SomeOne.repository.IslandReviewsRepository;
import com.example.SomeOne.repository.TravelPlansRepository;
import com.example.SomeOne.repository.TravelRecordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPlansService {

    private final IslandService islandService;
    private final TravelPlansRepository travelPlansRepository;
    private final TravelPlaceService travelPlaceService;
    private final UserService userService;
    private final WeatherService weatherService;

    @Transactional
    public SaveTravelResponse save(Long userId, TravelPlanRequest request) {
        Island island = islandService.findById(request.getIslandId());

        Users user = userService.findById(userId);

        TravelPlans travelPlan = new TravelPlans(user, request.getPlanName(), request.getStartDate(),
                request.getEndDate(), island);

        travelPlansRepository.save(travelPlan);

        return new SaveTravelResponse(travelPlan.getPlanId());
    }

    public List<GetPlansResponse> getPlan(Long userId) {
        Users user = userService.findById(userId);
        List<TravelPlans> planList = travelPlansRepository.findByUserOrderByStartDateDesc(user);

        return planList.stream().map(p -> new GetPlansResponse(p.getPlanId(), p.getPlan_name(), p.getIsland().getAddress(),
                p.getStartDate(), p.getEndDate(), p.getStatus())).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long userId, Long planId) {
        TravelPlans plan = travelPlansRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid plan ID: " + planId));

        if (plan.getUser().getUsers_id() != userId) {
            throw new IllegalArgumentException("Wrong user");
        }

        travelPlansRepository.delete(plan);
    }

    public TravelPlans findById(Long id) {
        return travelPlansRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
    }

    public GetTravelPlanResponse findTravelPlan(Long userId, Long planId) {
        TravelPlans travelPlans = findById(planId);
        String planName = travelPlans.getPlan_name();
        String islandName = travelPlans.getIsland().getName();
        List<TravelPlace> travelPlaceList = travelPlaceService.findByTravelPlan(userId, planId);
        LocalDate startDate = travelPlans.getStartDate();
        LocalDate endDate = travelPlans.getEndDate();

        WeatherNowDTO currentWeather = weatherService.getWeather(travelPlans.getIsland().getId());
        Double temperature = currentWeather.getTemperature();

        List<TravelPlaceResponse> responseList = travelPlaceList.stream().map(p -> {
            return new TravelPlaceResponse(
                    p.getPlace_id(),
                    p.getBusinesses().getBusiness_name(),
                    p.getBusinesses().getAddress(),
                    p.getBusinesses().getX_address(),
                    p.getBusinesses().getY_address(),
                    p.getBusinesses().getBusinessType(),
                    p.getDate(),
                    p.getPlaceOrder(),
                    p.getBusinesses().getImg_url()
            );
        }).collect(Collectors.toList());

        return new GetTravelPlanResponse(planName, islandName, startDate, endDate, temperature, responseList);
    }

    public class CoordinateParser {

        public static int parseCoordinate(String coordinate) {
            return (int) Math.floor(Double.parseDouble(coordinate));
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateStatus() {
        List<TravelPlans> startDateList = travelPlansRepository.findByStartDate(LocalDate.now());

        for (TravelPlans plan : startDateList) {
            plan.startTravel();
        }

        List<TravelPlans> finishDateList = travelPlansRepository.findByEndDate(LocalDate.now());

        for (TravelPlans plan : finishDateList) {
            plan.finishTravel();
        }
    }
}
