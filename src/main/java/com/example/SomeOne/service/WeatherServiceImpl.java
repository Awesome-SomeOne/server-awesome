package com.example.SomeOne.service;

import com.example.SomeOne.dao.WeatherDAO;
import com.example.SomeOne.dto.weather.WeatherNowDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Autowired
    private WeatherDAO weatherDAO;

    @Override
    public WeatherNowDTO getCurrentWeather(Double xCoordinate, Double yCoordinate) {
        // 기본 위치에 대한 날씨 정보를 반환하거나 필요에 따라 구현
        return null;
    }

    @Override
    public WeatherNowDTO getCurrentWeather(int nx, int ny) {
        return weatherDAO.getCurrentWeather(nx, ny);  // DAO에서 위도와 경도를 사용해 날씨 정보를 가져오도록 변경
    }

    @Override
    public WeatherNowDTO getWeather(Long islandId) {
        return weatherDAO.getWeather(islandId);  // DAO에서 위도와 경도를 사용해 날씨 정보를 가져오도록 변경
    }
}
