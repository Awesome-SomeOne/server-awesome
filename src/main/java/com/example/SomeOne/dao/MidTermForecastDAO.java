package com.example.SomeOne.dao;

import com.example.SomeOne.dto.MidTermForecastDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class MidTermForecastDAO {

    @Value("${api.midTermApiKey}")
    private String apiKey;

    private final String API_URL = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst";

    public MidTermForecastDTO getMidTermLandFcst(String regId) {
        RestTemplate restTemplate = new RestTemplate();

        // 현재 날짜와 시간을 가져와서 tmFc(발표시각)에 설정
        LocalDateTime now = LocalDateTime.now();
        String tmFc;

        // 현재 시각이 06:00 이후, 18:00 이전이라면 06:00 발표 시각을 사용
        if (now.getHour() >= 6 && now.getHour() < 18) {
            tmFc = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "0600";
        } else {
            // 그 외에는 18:00 발표 시각을 사용
            if (now.getHour() < 6) {
                // 06:00 이전인 경우, 전날 18:00 발표 시각을 사용
                tmFc = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "1800";
            } else {
                // 18:00 이후인 경우, 당일 18:00 발표 시각을 사용
                tmFc = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "1800";
            }
        }

        try {
            String url = API_URL + "?serviceKey=" + apiKey
                    + "&pageNo=1&numOfRows=10&dataType=JSON"
                    + "&regId=" + regId
                    + "&tmFc=" + tmFc;

            URI uri = new URI(url);

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                if (responseBody.contains("SERVICE_KEY_IS_NOT_REGISTERED_ERROR")) {
                    System.err.println("API Key is not registered or incorrect.");
                    return null;
                }
                return parseMidTermLandFcstResponse(responseBody);
            } else {
                // 에러 처리
                System.err.println("Failed to fetch data from API: " + response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private MidTermForecastDTO parseMidTermLandFcstResponse(String responseBody) {
        MidTermForecastDTO dto = new MidTermForecastDTO();

        try {
            JSONObject json;
            if (responseBody.trim().startsWith("<")) {
                json = XML.toJSONObject(responseBody);
            } else {
                json = new JSONObject(responseBody);
            }

            if (json.has("OpenAPI_ServiceResponse")) {
                JSONObject serviceResponse = json.getJSONObject("OpenAPI_ServiceResponse");
                if (serviceResponse.has("cmmMsgHeader")) {
                    JSONObject cmmMsgHeader = serviceResponse.getJSONObject("cmmMsgHeader");
                    String errMsg = cmmMsgHeader.optString("errMsg", "Unknown error");
                    System.err.println("Error in API response: " + errMsg);
                    return null;  // 또는 적절히 예외를 던지거나 에러 처리를 수행
                }
            }

            // 변환된 JSON에서 필요한 데이터 추출 (기존 JSON 파싱 로직 사용)
            parseJsonResponse(json, dto);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dto;
    }

    private void parseJsonResponse(JSONObject json, MidTermForecastDTO dto) throws JSONException {
        JSONObject response = json.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");
        JSONObject items = body.getJSONObject("items");

        // 3일차부터 7일차까지 데이터 파싱
        for (int day = 3; day <= 7; day++) {
            // 오전 날씨
            String morningWeather = items.optString("wf" + day + "Am", "정보 없음");

            // 오후 날씨
            String eveningWeather = items.optString("wf" + day + "Pm", "정보 없음");

            // 오전 강수 확률
            Integer morningRainProb = items.has("rnSt" + day + "Am") ? items.optInt("rnSt" + day + "Am", -1) : null;

            // 오후 강수 확률
            Integer eveningRainProb = items.has("rnSt" + day + "Pm") ? items.optInt("rnSt" + day + "Pm", -1) : null;

            // 강수량 계산 (만약 morningRainProb 또는 eveningRainProb가 null인 경우 -1을 기본값으로 사용)
            int rainfall = (morningRainProb != null ? morningRainProb : -1) + (eveningRainProb != null ? eveningRainProb : -1);

            // 온도와 습도 (기본 값 설정)
            int temperature = -1;
            int humidity = -1;

            // 날씨 상태
            String weatherCondition = morningWeather.equals(eveningWeather) ? morningWeather : morningWeather + " / " + eveningWeather;

            // DTO에 데이터를 저장
            dto.addForecast(day, "", morningWeather, eveningWeather, rainfall, temperature, humidity, weatherCondition);
        }
    }

}
