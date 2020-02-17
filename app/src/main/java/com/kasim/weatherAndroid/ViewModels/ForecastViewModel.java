package com.kasim.weatherAndroid.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kasim.weatherAndroid.Models.WeatherForecast;
import com.kasim.weatherAndroid.Service.WeatherRepository;

public class ForecastViewModel extends ViewModel {

    private MutableLiveData<WeatherForecast> mutableLiveData;
    private WeatherRepository weatherRepository;

    public void init(String lat, String lon, String lang, String units, String appId) {
        if (mutableLiveData != null) {
            return;
        }
        weatherRepository = WeatherRepository.getInstance();
        mutableLiveData = weatherRepository.getForecast(lat, lon, lang, units, appId);

    }

    public LiveData<WeatherForecast> getWeatherRepository() {
        return mutableLiveData;
    }

}
