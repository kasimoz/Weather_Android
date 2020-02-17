package com.kasim.weatherAndroid;

import com.kasim.weatherAndroid.Models.WeatherLocation;
import com.kasim.weatherAndroid.Service.RetrofitService;
import com.kasim.weatherAndroid.Service.WeatherApi;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class WeatherApiTest {

    @Test
    public void weather_Success() {

        WeatherApi weatherApi = RetrofitService.createService(WeatherApi.class);
        Call<WeatherLocation> call = weatherApi.getWeather("39.920776", "32.854139" ,"en","metric","<Your_App_Id>");

        try {
            Response<WeatherLocation> response = call.execute();
            WeatherLocation authResponse = response.body();
            Assert.assertNotNull("isDtNull",authResponse.getDt());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
