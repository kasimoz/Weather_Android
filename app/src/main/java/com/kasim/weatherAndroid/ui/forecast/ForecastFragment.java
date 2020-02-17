package com.kasim.weatherAndroid.ui.forecast;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasim.weatherAndroid.Adapters.ForecastAdapter;
import com.kasim.weatherAndroid.Contants;
import com.kasim.weatherAndroid.Helpers.RecyclerViewDivider;
import com.kasim.weatherAndroid.Helpers.SharedPreferenceManager;
import com.kasim.weatherAndroid.Helpers.Utils;
import com.kasim.weatherAndroid.Models.WeatherLocation;
import com.kasim.weatherAndroid.R;
import com.kasim.weatherAndroid.ViewModels.ForecastViewModel;

import java.util.ArrayList;
import java.util.Date;

public class ForecastFragment extends Fragment {

    double inHg = 0.030;
    private TextView feels_like, wind, humidity, pressure;
    private ForecastViewModel forecastViewModel;
    private ArrayList<WeatherLocation> list = new ArrayList<>();
    private ForecastAdapter forecastAdapter;
    private RecyclerView rvForecast;
    private ForecastFragmentViewModel forecastFragmentViewModel;
    private LinearLayout scrollLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_forecast, container, false);

        return root;
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        feels_like = root.findViewById(R.id.feels_like);
        pressure = root.findViewById(R.id.pressure);
        humidity = root.findViewById(R.id.humidity);
        wind = root.findViewById(R.id.wind);
        rvForecast = root.findViewById(R.id.rvForecast);
        scrollLayout = root.findViewById(R.id.scrollLayout);
        setupRecyclerView();
        if (Contants.getInstance().getWeatherLocation() != null) {
            feels_like.setText(Contants.getInstance().getWeatherLocation().getMain().getFeelsLike().intValue() + SharedPreferenceManager.getUnitSymbol(getActivity()));
            wind.setText(Contants.getInstance().getWeatherLocation().getWind().getSpeed().intValue() + " m/s");
            humidity.setText(Contants.getInstance().getWeatherLocation().getMain().getHumidity().intValue() + " %");
            pressure.setText(String.format("%.2f IN", Contants.getInstance().getWeatherLocation().getMain().getPressure() * inHg));
        }
        forecastFragmentViewModel = ViewModelProviders.of(getActivity()).get(ForecastFragmentViewModel.class);
        forecastFragmentViewModel.init(getActivity());
        forecastFragmentViewModel.getForecastData().observe(getActivity(), weatherForecast -> {
            if (weatherForecast != null && weatherForecast.getCod().intValue() == 200) {
                list.clear();
                list.addAll(weatherForecast.getList());
                forecastAdapter.notifyDataSetChanged();
                addScrollView();
            }
        });

        if (SharedPreferenceManager.getLatitude(getActivity()).isEmpty())
            return;

        if (new Date().getTime() - SharedPreferenceManager.getForecastRequestTime(getActivity()) > 0 && new Date().getTime() - SharedPreferenceManager.getForecastRequestTime(getActivity()) < (5 * 60 * 1000)) {
            return;
        }
        Handler hndler = new Handler();
        hndler.postDelayed(new Runnable() {
            @Override
            public void run() {
                forecastViewModel = ViewModelProviders.of(getActivity()).get(ForecastViewModel.class);
                forecastViewModel.init(SharedPreferenceManager.getLatitude(getActivity()), SharedPreferenceManager.getLongitude(getActivity()), SharedPreferenceManager.getLanguageCode(getActivity()), SharedPreferenceManager.getUnit(getActivity()), getString(R.string.appId));
                forecastViewModel.getWeatherRepository().observe(getActivity(), weatherForecast -> {
                    if (weatherForecast != null && weatherForecast.getCod().intValue() == 200) {
                        list.clear();
                        list.addAll(weatherForecast.getList());
                        forecastAdapter.notifyDataSetChanged();
                        SharedPreferenceManager.saveLastForecast(getActivity(), weatherForecast);
                        SharedPreferenceManager.setForecastRequestTime(getActivity(), new Date().getTime());
                        addScrollView();
                    }
                });
            }
        }, 500);
    }

    /**
     * Setup rvForecast
     */
    private void setupRecyclerView() {
        if (forecastAdapter == null) {
            forecastAdapter = new ForecastAdapter(getActivity(), list);
            rvForecast.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvForecast.setAdapter(forecastAdapter);
            rvForecast.setItemAnimator(new DefaultItemAnimator());
            rvForecast.setNestedScrollingEnabled(true);
            rvForecast.addItemDecoration(new RecyclerViewDivider(getActivity()));
        } else {
            forecastAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Add forecast data to scrollLayout
     */
    private void addScrollView() {
        float dip = 40f;
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        float viewWidth = (width - px) / 4;
        scrollLayout.removeAllViewsInLayout();
        if (list.size() > 0) {
            for (int i = 0; i < 9; i++) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.detail_day_item, scrollLayout, false);
                LinearLayout main = view.findViewById(R.id.main);
                ImageView icon = view.findViewById(R.id.icon);
                TextView time = view.findViewById(R.id.time);
                TextView temp = view.findViewById(R.id.temp);
                icon.setImageResource(Utils.getIconOfDay(list.get(i).getWeather().get(0)));
                time.setText(Utils.dateToTime(list.get(i).getDt_txt()));
                temp.setText(list.get(i).getMain().getTemp().intValue() + SharedPreferenceManager.getUnitSymbol(getActivity()));
                main.getLayoutParams().width = (int) viewWidth;
                main.requestLayout();
                scrollLayout.addView(view);
            }
        }
    }


}