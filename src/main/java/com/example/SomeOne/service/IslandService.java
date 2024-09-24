package com.example.SomeOne.service;

import com.example.SomeOne.domain.Island;
import com.example.SomeOne.dto.TravelPlans.response.RandomIslandResponse;
import com.example.SomeOne.dto.TravelPlans.response.FindIslandResponse;
import com.example.SomeOne.repository.IslandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IslandService {

    private final IslandRepository islandRepository;

    public List<FindIslandResponse> findIsland(String keyword) {
        List<Island> islandList = islandRepository.findByKeyword(keyword);

        return islandList.stream()
                .map(island -> new FindIslandResponse(island.getId(), island.getName(),
                        island.getAddress(), island.getImg_url())).collect(Collectors.toList());
    }

    public Island findById(Long id) {
        return islandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
    }

    public RandomIslandResponse randomIsland() {
        List<Island> islands = islandRepository.findAll();
        Collections.shuffle(islands);

        Island island = islands.get(0);
        return new RandomIslandResponse(island.getId(), island.getName(), island.getAddress(), island.getImg_url());
    }
}
