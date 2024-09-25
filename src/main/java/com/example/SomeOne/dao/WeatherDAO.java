package com.example.SomeOne.dao;

import com.example.SomeOne.dto.weather.WeatherNowDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class WeatherDAO {

    @Value("${api.key}")
    private String apikey;

    private final String API_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";

    public WeatherNowDTO getCurrentWeather(int nx, int ny) {
        String SERVICE_KEY = apikey; // 서비스 키 설정
        RestTemplate restTemplate = new RestTemplate();

        // 현재 날짜와 시간을 가져와서 base_date와 base_time에 설정
        LocalDateTime now = LocalDateTime.now();
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String baseTime;
        int minute = now.getMinute();
        if (minute >= 10) {
            // 예: 10분 이후에 요청 시, 현재 시간을 사용
            baseTime = now.format(DateTimeFormatter.ofPattern("HH00"));
        } else {
            baseTime = now.minusHours(1).format(DateTimeFormatter.ofPattern("HH00"));
        }

        URI url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true)  // true를 사용하여 인코딩 문제 방지
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return parseWeatherResponse(response.getBody());
        } else {
            // 에러 처리
            return null;
        }
    }

    public WeatherNowDTO getWeather(Long islandId) {
        String SERVICE_KEY = apikey; // 서비스 키 설정
        RestTemplate restTemplate = new RestTemplate();

        // 현재 날짜와 시간을 가져와서 base_date와 base_time에 설정
        LocalDateTime now = LocalDateTime.now();
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String baseTime;
        int minute = now.getMinute();
        if (minute >= 10) {
            // 예: 10분 이후에 요청 시, 현재 시간을 사용
            baseTime = now.format(DateTimeFormatter.ofPattern("HH00"));
        } else {
            baseTime = now.minusHours(1).format(DateTimeFormatter.ofPattern("HH00"));
        }

        // islandId에 따라 nx, ny 매핑
        int[] coordinates = getCoordinatesByIslandId(islandId);
        if (coordinates == null) {
            return null;
        }
        int nx = coordinates[0];
        int ny = coordinates[1];

        URI url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true)  // true를 사용하여 인코딩 문제 방지
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return parseWeatherResponse(response.getBody());
        } else {
            // 에러 처리
            return null;
        }
    }

    private int[] getCoordinatesByIslandId(Long islandId) {
        switch (islandId.intValue()) {
            case 1: return new int[]{51, 130}; // 강화도
            case 2: return new int[]{90, 69};  // 거제도
            case 3: return new int[]{77, 68};  // 남해도
            case 4: return new int[]{56, 92};  // 선유도
            case 5: return new int[]{52, 125}; // 영종도
            case 6: return new int[]{57, 56};  // 완도
            case 7: return new int[]{84, 63};  // 욕지도
            case 8: return new int[]{52, 38};  // 제주도
            case 9: return new int[]{48, 59};  // 진도
            case 10: return new int[]{33, 64}; // 흑산도
            default: return null;
        }
    }


    private WeatherNowDTO parseWeatherResponse(String responseBody) {
        WeatherNowDTO dto = new WeatherNowDTO();
        try {
            // JSON 응답 처리
            JSONObject json = new JSONObject(responseBody);
            JSONObject response = json.getJSONObject("response");
            JSONObject header = response.getJSONObject("header");
            JSONObject body = response.getJSONObject("body");
            JSONArray items = body.getJSONObject("items").getJSONArray("item");

            // Header 정보 추출
            dto.setResultCode(header.getString("resultCode"));
            dto.setResultMsg(header.getString("resultMsg"));

            // 첫 번째 아이템에서 baseDate와 baseTime 추출
            if (items.length() > 0) {
                JSONObject firstItem = items.getJSONObject(0);
                dto.setBaseDate(firstItem.getString("baseDate"));
                dto.setBaseTime(firstItem.getString("baseTime"));
            }

            // Body 정보 추출
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String category = item.getString("category");
                String obsrValue = item.getString("obsrValue");

                // 필요한 정보만 처리 (온도, 강수량, 습도 등)
                switch (category) {
                    case "T1H": // 온도
                        dto.setTemperature(Double.parseDouble(obsrValue));
                        break;
                    case "RN1": // 강수량
                        dto.setRainfall(Double.parseDouble(obsrValue));
                        break;
                    case "REH": // 습도
                        dto.setHumidity(Double.parseDouble(obsrValue));
                        break;
                    // 필요한 경우 다른 필드 추가
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    private String parseWeatherCondition(String code) {
        switch (code) {
            case "1":
                return "맑음";
            case "3":
                return "구름 많음";
            case "4":
                return "흐림";
            default:
                return "알 수 없음";
        }
    }
}
