package com.example.SomeOne.service;

import com.example.SomeOne.domain.Island;
import com.example.SomeOne.repository.IslandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IslandService {

    private final IslandRepository islandRepository;

    public List<String> findIsland(String keyword) {
        return islandRepository.findByKeyword(keyword).stream().map(i -> i.getIsland_name()).collect(Collectors.toList());
    }

    public Island findByName(String name) {
        return islandRepository.findByIslandName(name);
    }
}
