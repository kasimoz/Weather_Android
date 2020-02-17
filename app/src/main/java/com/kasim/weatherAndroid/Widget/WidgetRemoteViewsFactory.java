package com.kasim.weatherAndroid.Widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.kasim.weatherAndroid.Helpers.SharedPreferenceManager;
import com.kasim.weatherAndroid.Helpers.Utils;
import com.kasim.weatherAndroid.Models.WeatherForecast;
import com.kasim.weatherAndroid.R;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    Context context;
    WeatherForecast weatherForecast;

    public WidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        context = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        weatherForecast = SharedPreferenceManager.getLastForecast(context);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return weatherForecast.getList().size() > 4 ? 4 : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.detail_day_item);
        views.setTextViewText(R.id.time, Utils.dateToTime(weatherForecast.getList().get(position).getDt_txt()));
        views.setTextViewText(R.id.temp, weatherForecast.getList().get(position).getMain().getTemp().intValue() + SharedPreferenceManager.getUnitSymbol(context));
        views.setImageViewResource(R.id.icon, Utils.getIconOfDay(weatherForecast.getList().get(position).getWeather().get(0)));
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
