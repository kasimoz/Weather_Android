package com.kasim.weatherAndroid;


import com.kasim.weatherAndroid.Models.WeatherLocation;

public class Contants {
    private static Contants contants;

    public static Contants getInstance() {
        if (contants == null) {
            contants = new Contants();
        }
        return contants;
    }

    private WeatherLocation weatherLocation;

    public WeatherLocation getWeatherLocation() {
        return weatherLocation;
    }

    public void setWeatherLocation(WeatherLocation weatherLocation) {
        this.weatherLocation = weatherLocation;
    }

}
