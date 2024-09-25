package com.example.SomeOne.service;

import com.example.SomeOne.dto.weather.WeatherNowDTO;

public interface WeatherService {
    WeatherNowDTO getCurrentWeather(Double xCoordinate, Double yCoordinate);

    WeatherNowDTO getCurrentWeather(int nx, int ny);
    WeatherNowDTO getWeather(Long islandId);
}
