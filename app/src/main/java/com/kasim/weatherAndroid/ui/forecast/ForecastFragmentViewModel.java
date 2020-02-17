package com.kasim.weatherAndroid.ui.forecast;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kasim.weatherAndroid.Helpers.SharedPreferenceManager;
import com.kasim.weatherAndroid.Models.WeatherForecast;

public class ForecastFragmentViewModel extends ViewModel {

    private MutableLiveData<WeatherForecast> forecastData;

    public void init(Context context) {
        forecastData = new MutableLiveData<>();
        forecastData.setValue(SharedPreferenceManager.getLastForecast(context));
    }

    public LiveData<WeatherForecast> getForecastData() {
        return forecastData;
    }
}