package com.kasim.weatherAndroid.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kasim.weatherAndroid.Models.WeatherForecast;
import com.kasim.weatherAndroid.Models.WeatherLocation;

import java.lang.reflect.Modifier;
import java.util.Date;

public class SharedPreferenceManager {

    public static SharedPreferences getPrivatePreferences(Context context) {
        return context.getSharedPreferences("WeatherApp", Context.MODE_PRIVATE);
    }

    public static void saveLastWeather(Context context, WeatherLocation weatherLocation) {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
        getPrivatePreferences(context).edit()
                .putString("Weather", gson.toJson(weatherLocation))
                .apply();
    }

    public static void saveLastForecast(Context context, WeatherForecast weatherForecast) {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
        getPrivatePreferences(context).edit()
                .putString("Forecast", gson.toJson(weatherForecast))
                .apply();
    }

    public static WeatherLocation getLastWeather(Context context) {
        Gson gson = new Gson();
        if (getPrivatePreferences(context).getString("Weather", "").equals("")) {
            return null;
        } else {
            return gson.fromJson(getPrivatePreferences(context).getString("Weather", ""), WeatherLocation.class);
        }
    }

    public static WeatherForecast getLastForecast(Context context) {
        Gson gson = new Gson();
        if (getPrivatePreferences(context).getString("Forecast", "").equals("")) {
            return null;
        } else {
            return gson.fromJson(getPrivatePreferences(context).getString("Forecast", ""), WeatherForecast.class);
        }
    }

    public static void setLanguage(Context context, String language) {
        getPrivatePreferences(context).edit()
                .putString("Language", language)
                .apply();
    }

    public static String getLanguage(Context context) {
        return getPrivatePreferences(context).getString("Language", "English");
    }

    public static void setLanguageCode(Context context, String code) {
        getPrivatePreferences(context).edit()
                .putString("Code", code)
                .apply();
    }

    public static String getLanguageCode(Context context) {
        return getPrivatePreferences(context).getString("Code", "en");
    }

    public static void setUnit(Context context, String unit) {
        getPrivatePreferences(context).edit()
                .putString("Unit", unit)
                .apply();
    }

    public static String getUnit(Context context) {
        return getPrivatePreferences(context).getString("Unit", "metric");
    }

    public static void setUnitSymbol(Context context, String unit) {
        getPrivatePreferences(context).edit()
                .putString("Symbol", unit)
                .apply();
    }

    public static String getUnitSymbol(Context context) {
        return getPrivatePreferences(context).getString("Symbol", "°C");
    }

    public static void setCity(Context context, String city) {
        getPrivatePreferences(context).edit()
                .putString("City", city)
                .apply();
    }

    public static String getCity(Context context) {
        return getPrivatePreferences(context).getString("City", "");
    }

    public static void setDistrict(Context context, String district) {
        getPrivatePreferences(context).edit()
                .putString("District", district)
                .apply();
    }

    public static String getDistrict(Context context) {
        return getPrivatePreferences(context).getString("District", "");
    }

    public static void setWeatherRequestTime(Context context, long time) {
        getPrivatePreferences(context).edit()
                .putLong("WeatherRequestTime", time)
                .apply();
    }

    public static long getWeatherRequestTime(Context context) {
        return getPrivatePreferences(context).getLong("WeatherRequestTime", new Date().getTime());
    }

    public static void setForecastRequestTime(Context context, long time) {
        getPrivatePreferences(context).edit()
                .putLong("ForecastRequestTime", time)
                .apply();
    }

    public static long getForecastRequestTime(Context context) {
        return getPrivatePreferences(context).getLong("ForecastRequestTime", new Date().getTime());
    }

    public static void setLastLocation(Context context, Location location) {
        getPrivatePreferences(context).edit()
                .putString("Latitude", String.valueOf(location.getLatitude()))
                .putString("Longitude", String.valueOf(location.getLongitude()))
                .apply();
    }

    public static String getLatitude(Context context) {
        return getPrivatePreferences(context).getString("Latitude", "");
    }

    public static String getLongitude(Context context) {
        return getPrivatePreferences(context).getString("Longitude", "");
    }
}
