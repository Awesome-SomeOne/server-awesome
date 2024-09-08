package com.example.SomeOne.dto.weather;

import java.util.ArrayList;
import java.util.List;

public class MidTermForecastDTO {

    private List<DayForecast> forecasts = new ArrayList<>();

    public void addForecast(int day, String date, String morningWeather, String eveningWeather, int rainfall, int temperature, int humidity, String weatherCondition) {
        DayForecast forecast = new DayForecast(day, date, morningWeather, eveningWeather, rainfall, temperature, humidity, weatherCondition);
        forecasts.add(forecast);
    }

    public static class DayForecast {
        private int day;
        private String date;
        private String morningWeather;
        private String eveningWeather;
        private int rainfall;
        private int temperature;
        private int humidity;
        private String weatherCondition; // 추가된 필드

        public DayForecast(int day, String date, String morningWeather, String eveningWeather, int rainfall, int temperature, int humidity, String weatherCondition) {
            this.day = day;
            this.date = date;
            this.morningWeather = morningWeather;
            this.eveningWeather = eveningWeather;
            this.rainfall = rainfall;
            this.temperature = temperature;
            this.humidity = humidity;
            this.weatherCondition = weatherCondition; // 추가된 필드 초기화
        }

        // Getters and setters
        public int getDay() { return day; }
        public void setDay(int day) { this.day = day; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getMorningWeather() { return morningWeather; }
        public void setMorningWeather(String morningWeather) { this.morningWeather = morningWeather; }
        public String getEveningWeather() { return eveningWeather; }
        public void setEveningWeather(String eveningWeather) { this.eveningWeather = eveningWeather; }
        public int getRainfall() { return rainfall; }
        public void setRainfall(int rainfall) { this.rainfall = rainfall; }
        public int getTemperature() { return temperature; }
        public void setTemperature(int temperature) { this.temperature = temperature; }
        public int getHumidity() { return humidity; }
        public void setHumidity(int humidity) { this.humidity = humidity; }
        public String getWeatherCondition() { return weatherCondition; }
        public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }
    }

    public List<DayForecast> getForecasts() {
        return forecasts;
    }
}
