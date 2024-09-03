package com.example.SomeOne.service;

import com.example.SomeOne.dao.WeatherDAO;
import com.example.SomeOne.dto.weather.WeatherNowDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Autowired
    private WeatherDAO weatherDAO;

    @Override
    public WeatherNowDTO getCurrentWeather() {
        // 기본 위치에 대한 날씨 정보를 반환하거나 필요에 따라 구현
        return null;
    }

    @Override
    public WeatherNowDTO getCurrentWeather(int nx, int ny) {
        return weatherDAO.getCurrentWeather(nx, ny);  // DAO에서 위도와 경도를 사용해 날씨 정보를 가져오도록 변경
    }
}
