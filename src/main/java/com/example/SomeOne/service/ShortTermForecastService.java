package com.example.SomeOne.service;

import com.example.SomeOne.dao.ShortTermForecastDAO;
import com.example.SomeOne.dto.weather.ShortTermForecastDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShortTermForecastService {

    private final ShortTermForecastDAO shortTermForecastDAO;

    @Autowired
    public ShortTermForecastService(ShortTermForecastDAO shortTermForecastDAO) {
        this.shortTermForecastDAO = shortTermForecastDAO;
    }

    public ShortTermForecastDTO getShortTermForecast(int nx, int ny) {
        return shortTermForecastDAO.getShortTermForecast(nx, ny);
    }
}
