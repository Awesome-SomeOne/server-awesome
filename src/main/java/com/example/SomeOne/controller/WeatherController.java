package com.example.SomeOne.controller;

import com.example.SomeOne.dto.weather.MidTermForecastDTO;
import com.example.SomeOne.dto.weather.ShortTermForecastDTO;
import com.example.SomeOne.dto.weather.WeatherNowDTO;
import com.example.SomeOne.service.MidTermForecastService;
import com.example.SomeOne.service.ShortTermForecastService;
import com.example.SomeOne.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;
    private final ShortTermForecastService shortTermForecastService;
    private final MidTermForecastService midTermForecastService;

    @Autowired
    public WeatherController(WeatherService weatherService, ShortTermForecastService shortTermForecastService, MidTermForecastService midTermForecastService) {
        this.weatherService = weatherService;
        this.shortTermForecastService = shortTermForecastService;
        this.midTermForecastService = midTermForecastService;
    }

    @GetMapping("/current")
    public WeatherNowDTO getCurrentWeather(@RequestParam int nx, @RequestParam int ny) {
        return weatherService.getCurrentWeather(nx, ny);
    }

    @GetMapping("/short-term-forecast")
    public ShortTermForecastDTO getShortTermForecast(@RequestParam int nx, @RequestParam int ny) {
        return shortTermForecastService.getShortTermForecast(nx, ny);
    }

    @GetMapping("/mid-term-forecast")
    public MidTermForecastDTO getMidTermLandFcst(@RequestParam String regId) {
        return midTermForecastService.getMidTermLandFcst(regId);
    }


}
