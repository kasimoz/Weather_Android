package com.kasim.weatherAndroid.ui.weather;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kasim.weatherAndroid.Helpers.SharedPreferenceManager;
import com.kasim.weatherAndroid.Models.WeatherLocation;

public class WeatherFragmentViewModel extends ViewModel {

    private MutableLiveData<WeatherLocation> weatherData;

    public void init(Context context) {
        weatherData = new MutableLiveData<>();
        weatherData.setValue(SharedPreferenceManager.getLastWeather(context));
    }

    public LiveData<WeatherLocation> getWeatherData() {
        return weatherData;
    }
}