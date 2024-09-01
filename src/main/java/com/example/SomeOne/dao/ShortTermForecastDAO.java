package com.example.SomeOne.dao;

import com.example.SomeOne.dto.ShortTermForecastDTO;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class ShortTermForecastDAO {

    @Value("${api.key}")
    private String apiKey;

    private final String API_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

    public ShortTermForecastDTO getShortTermForecast(int nx, int ny) {
        RestTemplate restTemplate = new RestTemplate();

        // 현재 날짜와 시간을 가져와서 base_date와 base_time에 설정
        LocalDateTime now = LocalDateTime.now();
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String baseTime;
        int minute = now.getMinute();
        if (minute >= 10) {
            // 10분 이후에 요청 시, 현재 시간을 사용
            baseTime = now.format(DateTimeFormatter.ofPattern("HH00"));
        } else {
            // 10분 이전에 요청 시, 이전 시간을 사용
            baseTime = now.minusHours(1).format(DateTimeFormatter.ofPattern("HH00"));
        }

        String url = API_URL + "?serviceKey=" + apiKey
                + "&numOfRows=10&pageNo=1&dataType=JSON"
                + "&base_date=" + baseDate
                + "&base_time=" + baseTime
                + "&nx=" + nx
                + "&ny=" + ny;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return parseShortTermForecastResponse(response.getBody());
        } else {
            // 에러 처리
            return null;
        }
    }

    private ShortTermForecastDTO parseShortTermForecastResponse(String responseBody) {
        ShortTermForecastDTO dto = new ShortTermForecastDTO();
        try {
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
                    case "TMP": // 온도
                        dto.setTemperature(Double.parseDouble(obsrValue));
                        break;
                    case "POP": // 강수확률
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
