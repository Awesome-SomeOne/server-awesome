package com.example.SomeOne.controller;

import com.example.SomeOne.dto.WeatherNowDTO;
import com.example.SomeOne.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/current")
    public WeatherNowDTO getCurrentWeather(@RequestParam int nx, @RequestParam int ny) {
        return weatherService.getCurrentWeather(nx, ny);
    }

}
