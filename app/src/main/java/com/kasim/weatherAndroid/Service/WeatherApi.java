package com.kasim.weatherAndroid.Service;

import com.kasim.weatherAndroid.Models.WeatherForecast;
import com.kasim.weatherAndroid.Models.WeatherLocation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<WeatherLocation> getWeather(@Query("lat") String lat,
                                     @Query("lon") String lon,
                                     @Query("lang") String lang,
                                     @Query("units") String units,
                                     @Query("APPID") String appId);


    @GET("forecast")
    Call<WeatherForecast> getForecast(@Query("lat") String lat,
                                      @Query("lon") String lon,
                                      @Query("lang") String lang,
                                      @Query("units") String units,
                                      @Query("APPID") String appId);
}
