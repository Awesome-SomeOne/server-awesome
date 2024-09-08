//package com.example.SomeOne.controller;
//
//import com.example.SomeOne.dto.TravelRecords.Response.TravelRecordResponse;
//import com.example.SomeOne.service.MapService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/maps")
//@RequiredArgsConstructor
//public class MapController {
//
//    private final MapService mapService;
//
//    // 여행 장소 마커를 지도에 표시하기 위한 API
//    @GetMapping("/travel-records")
//    public List<TravelRecordResponse> getTravelRecordLocations() {
//        return mapService.getTravelRecordLocations();
//    }
//
//    // 특정 키워드로 식당 및 숙박 검색하기 위한 API
//    @GetMapping("/places")
//    public String searchPlaces(@RequestParam String keyword) {
//        return mapService.searchPlaces(keyword);
//    }
//}