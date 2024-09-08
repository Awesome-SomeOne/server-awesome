//package com.example.SomeOne.service;
//
//import com.example.SomeOne.domain.TravelRecords;
//import com.example.SomeOne.dto.TravelRecords.Response.TravelRecordResponse;
//import com.example.SomeOne.repository.TravelRecordsRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.http.HttpHeaders;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class MapService {
//
//    private final TravelRecordsRepository travelRecordsRepository;
//    private final String KAKAO_API_KEY = "YOUR_KAKAO_API_KEY"; // 카카오 API 키
//
//    // 여행 장소 마커 표시를 위한 서비스 메소드
//    public List<TravelRecordResponse> getTravelRecordLocations() {
//        List<TravelRecords> records = travelRecordsRepository.findAll();
//        return records.stream()
//                .map(record -> new TravelRecordResponse(record))
//                .collect(Collectors.toList());
//    }
//
//    // 카카오 API를 사용하여 식당 및 숙박 검색
//    public String searchPlaces(String keyword) {
//        String url = String.format("https://dapi.kakao.com/v2/local/search/keyword.json?query=%s", keyword);
//        RestTemplate restTemplate = new RestTemplate();
//        return restTemplate.getForObject(url, String.class, getHeaders());
//    }
//
//    // 카카오 API 호출을 위한 헤더 설정
//    private HttpHeaders getHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);
//        return headers;
//    }
//}
