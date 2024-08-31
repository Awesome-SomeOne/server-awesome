package com.example.SomeOne.dao;

import com.example.SomeOne.dto.WeatherNowDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class WeatherDAO {
    @Value("${api.key}")
    private String apikey;

    private final String API_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";

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
            // 예: 10분 이전에 요청 시, 이전 시간을 사용
            baseTime = now.minusHours(1).format(DateTimeFormatter.ofPattern("HH00"));
        }

        String url = API_URL + "?serviceKey=" + SERVICE_KEY
                + "&numOfRows=10&pageNo=1&dataType=JSON"
                + "&base_date=" + baseDate
                + "&base_time=" + baseTime
                + "&nx=" + nx
                + "&ny=" + ny;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return parseWeatherResponse(response.getBody());
        } else {
            // 에러 처리
            return null;
        }
    }

    private WeatherNowDTO parseWeatherResponse(String responseBody) {
        WeatherNowDTO dto = new WeatherNowDTO();
        try {
            // JSON 형식으로 파싱
            JSONObject json = new JSONObject(responseBody);
            JSONObject response = json.getJSONObject("response");
            JSONObject header = response.getJSONObject("header");
            JSONObject body = response.getJSONObject("body");
            JSONArray items = body.getJSONObject("items").getJSONArray("item");

            // Header 정보 추출
            dto.setResultCode(header.getString("resultCode"));
            dto.setResultMsg(header.getString("resultMsg"));

            // Body 정보 추출
            dto.setBaseDate(body.getString("baseDate"));
            dto.setBaseTime(body.getString("baseTime"));

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String category = item.getString("category");
                String obsrValue = item.getString("obsrValue");

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
                    case "SKY": // 날씨 상태
                        dto.setWeatherCondition(parseWeatherCondition(obsrValue));
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
