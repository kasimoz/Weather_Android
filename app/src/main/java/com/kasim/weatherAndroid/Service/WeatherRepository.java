package com.kasim.weatherAndroid.Service;

import androidx.lifecycle.MutableLiveData;

import com.kasim.weatherAndroid.Models.WeatherForecast;
import com.kasim.weatherAndroid.Models.WeatherLocation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {
    private static WeatherRepository weatherRepository;

    public static WeatherRepository getInstance() {
        if (weatherRepository == null) {
            weatherRepository = new WeatherRepository();
        }
        return weatherRepository;
    }

    private WeatherApi weatherApi;

    public WeatherRepository() {
        weatherApi = RetrofitService.createService(WeatherApi.class);
    }

    public MutableLiveData<WeatherLocation> getWeather(String lat, String lon, String lang, String units, String appId) {
        final MutableLiveData<WeatherLocation> weatherData = new MutableLiveData<>();
        weatherApi.getWeather(lat, lon, lang, units, appId).enqueue(new Callback<WeatherLocation>() {
            @Override
            public void onResponse(Call<WeatherLocation> call,
                                   Response<WeatherLocation> response) {
                if (response.isSuccessful()) {
                    weatherData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherLocation> call, Throwable t) {
                weatherData.setValue(null);
            }
        });

        return weatherData;
    }

    public MutableLiveData<WeatherForecast> getForecast(String lat, String lon, String lang, String units, String appId) {
        final MutableLiveData<WeatherForecast> forecastData = new MutableLiveData<>();
        weatherApi.getForecast(lat, lon, lang, units, appId).enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(Call<WeatherForecast> call,
                                   Response<WeatherForecast> response) {
                if (response.isSuccessful()) {
                    forecastData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherForecast> call, Throwable t) {
                forecastData.setValue(null);
            }
        });

        return forecastData;
    }
}
