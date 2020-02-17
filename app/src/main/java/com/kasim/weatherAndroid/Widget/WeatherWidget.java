package com.kasim.weatherAndroid.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.kasim.weatherAndroid.Helpers.SharedPreferenceManager;
import com.kasim.weatherAndroid.Models.WeatherForecast;
import com.kasim.weatherAndroid.R;
import com.kasim.weatherAndroid.Service.RetrofitService;
import com.kasim.weatherAndroid.Service.WeatherApi;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {
    private static final String REFRESH = "refreshButtonClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, false);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        if (REFRESH.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            updateAppWidget(context, appWidgetManager, intent.getIntExtra("appWidgetId", 0), true);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action, int appWidgetId) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        intent.putExtra("appWidgetId", appWidgetId);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    protected void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, boolean isUpdateClick) {
        if (SharedPreferenceManager.getLatitude(context).isEmpty())
            return;
        WeatherApi weatherApi = RetrofitService.createService(WeatherApi.class);
        weatherApi.getForecast(SharedPreferenceManager.getLatitude(context), SharedPreferenceManager.getLongitude(context), SharedPreferenceManager.getLanguageCode(context), SharedPreferenceManager.getUnit(context), context.getString(R.string.appId)).enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(Call<WeatherForecast> call,
                                   Response<WeatherForecast> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCod().intValue() == 200) {
                    SharedPreferenceManager.saveLastForecast(context, response.body());
                    SharedPreferenceManager.setForecastRequestTime(context, new Date().getTime());
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
                    Intent intent = new Intent(context, GridWidgetService.class);
                    views.setRemoteAdapter(R.id.gridView, intent);
                    views.setEmptyView(R.id.gridView, R.id.gridView);
                    views.setOnClickPendingIntent(R.id.imageButton, getPendingSelfIntent(context, REFRESH, appWidgetId));
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }

                if (isUpdateClick) {
                    ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WeatherWidget.class.getName());
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.gridView);
                }
            }

            @Override
            public void onFailure(Call<WeatherForecast> call, Throwable t) {

            }
        });

    }

}

