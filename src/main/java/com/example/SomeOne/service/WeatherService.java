package com.example.SomeOne.service;

import com.example.SomeOne.dto.weather.WeatherNowDTO;

public interface WeatherService {
    WeatherNowDTO getCurrentWeather();

    WeatherNowDTO getCurrentWeather(int nx, int ny);
}
