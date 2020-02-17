package com.kasim.weatherAndroid.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kasim.weatherAndroid.Models.WeatherLocation;
import com.kasim.weatherAndroid.Service.WeatherRepository;

public class WeatherViewModel extends ViewModel {

    private MutableLiveData<WeatherLocation> mutableLiveData;
    private WeatherRepository weatherRepository;

    public void init(String lat, String lon, String lang, String units, String appId) {
        if (mutableLiveData != null) {
            return;
        }
        weatherRepository = WeatherRepository.getInstance();
        mutableLiveData = weatherRepository.getWeather(lat, lon, lang, units, appId);

    }

    public LiveData<WeatherLocation> getWeatherRepository() {
        return mutableLiveData;
    }

}
