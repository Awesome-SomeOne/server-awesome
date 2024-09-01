package com.example.SomeOne.service;

import com.example.SomeOne.dao.MidTermForecastDAO;
import com.example.SomeOne.dto.MidTermForecastDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MidTermForecastService {

    private final MidTermForecastDAO midTermForecastDAO;

    @Autowired
    public MidTermForecastService(MidTermForecastDAO midTermForecastDAO) {
        this.midTermForecastDAO = midTermForecastDAO;
    }

    public MidTermForecastDTO getMidTermLandFcst(String regId) {
        return midTermForecastDAO.getMidTermLandFcst(regId);
    }
}
