package com.kasim.weatherAndroid.ui.weather;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kasim.weatherAndroid.Contants;
import com.kasim.weatherAndroid.Helpers.SharedPreferenceManager;
import com.kasim.weatherAndroid.Helpers.Utils;
import com.kasim.weatherAndroid.Models.WeatherLocation;
import com.kasim.weatherAndroid.R;
import com.kasim.weatherAndroid.ViewModels.WeatherViewModel;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherFragment extends Fragment implements LocationListener {
    private LocationManager locationManager;
    private WeatherFragmentViewModel weatherFragmentViewModel;
    private WeatherViewModel weatherViewModel;
    private IntentFilter intentFilter;
    private ImageView background, night, icon;
    private TextView city, district, description, temp_max_min, temp, sunrise, sunset;
    private SeekBar seekBar;
    private BottomNavigationView navView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        weatherFragmentViewModel = ViewModelProviders.of(this).get(WeatherFragmentViewModel.class);
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        View root = inflater.inflate(R.layout.fragment_weather, container, false);
        temp = root.findViewById(R.id.temp);
        background = getActivity().findViewById(R.id.weather_backgroud);
        night = getActivity().findViewById(R.id.weather_backgroud_night);
        navView = getActivity().findViewById(R.id.nav_view);
        icon = root.findViewById(R.id.weather_icon);
        city = root.findViewById(R.id.city);
        district = root.findViewById(R.id.district);
        description = root.findViewById(R.id.description);
        temp_max_min = root.findViewById(R.id.temp_max_min);
        sunrise = root.findViewById(R.id.sunrise);
        sunset = root.findViewById(R.id.sunset);
        seekBar = root.findViewById(R.id.seekBar);
        weatherFragmentViewModel.init(getActivity());
        weatherFragmentViewModel.getWeatherData().observe(this, weatherLocation -> {
            if (weatherLocation != null) {
                setAllViews(weatherLocation);
            }
            city.setText(SharedPreferenceManager.getCity(getActivity()));
            district.setText(SharedPreferenceManager.getDistrict(getActivity()));
        });

        return root;
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        getActivity().registerReceiver(timeChangedReceiver, intentFilter);

        // Disable seekBar touch.
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        666);
            } else {
                locationManager.requestLocationUpdates(isNetworkEnabled ? LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER, 60, 1000, this);
            }
        } else {
            locationManager.requestLocationUpdates(isNetworkEnabled ? LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER, 60, 1000, this);
        }

        if (!isLocationEnabled(getActivity()) && !SharedPreferenceManager.getLatitude(getActivity()).isEmpty()) {
            if (new Date().getTime() - SharedPreferenceManager.getWeatherRequestTime(getActivity()) > 0 && new Date().getTime() - SharedPreferenceManager.getWeatherRequestTime(getActivity()) < (5 * 60 * 1000)) {
                return;
            }
            Location location = new Location(LocationManager.NETWORK_PROVIDER);
            location.setLatitude(Double.parseDouble(SharedPreferenceManager.getLatitude(getActivity())));
            location.setLongitude(Double.parseDouble(SharedPreferenceManager.getLongitude(getActivity())));
            getWeatherInformation(location);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(timeChangedReceiver);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 666: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        locationManager.requestLocationUpdates(isNetworkEnabled ? LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER, 60, 1000, this);
                    }
                } else {

                }
                return;
            }
        }
    }


    /**
     * Check location mode on/off
     *
     * @param context
     * @return on/off
     */
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }


    @Override
    public void onLocationChanged(Location location) {
        if (new Date().getTime() - SharedPreferenceManager.getWeatherRequestTime(getActivity()) > 0 && new Date().getTime() - SharedPreferenceManager.getWeatherRequestTime(getActivity()) < (5 * 60 * 1000)) {
            return;
        }
        SharedPreferenceManager.setLastLocation(getActivity(), location);
        getWeatherInformation(location);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    /**
     * Get weather data when location changed
     * Time period should be at least five minutes for next weather data
     *
     * @param location
     */

    public void getWeatherInformation(Location location) {
        weatherViewModel.init(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), SharedPreferenceManager.getLanguageCode(getActivity()), SharedPreferenceManager.getUnit(getActivity()), getString(R.string.appId));
        weatherViewModel.getWeatherRepository().observe(this, weatherLocation -> {
            if (weatherLocation != null) {
                SharedPreferenceManager.saveLastWeather(getActivity(), weatherLocation);
                setAllViews(weatherLocation);
                SharedPreferenceManager.setWeatherRequestTime(getActivity(), new Date().getTime());
            }
            setCityAndDistrict(location);
        });
    }

    /**
     * Show city and district
     *
     * @param location
     */
    public void setCityAndDistrict(Location location) {
        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (address.size() > 0) {
                city.setText(address.get(0).getAdminArea());
                district.setText(address.get(0).getSubAdminArea());
                SharedPreferenceManager.setCity(getActivity(), city.getText().toString());
                SharedPreferenceManager.setDistrict(getActivity(), district.getText().toString());
            }
        } catch (IOException e) {
            // Handle IOException
        } catch (NullPointerException e) {
            // Handle NullPointerException
        }
    }

    /**
     * Reload weather data to all views
     *
     * @param weatherLocation
     */
    public void setAllViews(WeatherLocation weatherLocation) {
        Contants.getInstance().setWeatherLocation(weatherLocation);
        int color = R.color.colorClearSky;
        temp.setText(String.valueOf(weatherLocation.getMain().getTemp().intValue()) + SharedPreferenceManager.getUnitSymbol(getActivity()));
        //district.setText(weatherLocation.getName());
        description.setText(weatherLocation.getWeather().get(0).getDescription());
        sunrise.setText(Utils.millisecondToHour(weatherLocation.getSys().getSunrise() * 1000));
        sunset.setText(Utils.millisecondToHour(weatherLocation.getSys().getSunset() * 1000));
        int max = (int) ((weatherLocation.getSys().getSunset() - weatherLocation.getSys().getSunrise()) / 60);
        seekBar.setMax(max);
        int progress = (int) (((new Date().getTime() / 1000) - weatherLocation.getSys().getSunrise()) / 60);
        seekBar.setProgress(progress);
        boolean day = weatherLocation.getWeather().get(0).getIcon().contains("d");
        seekBar.setThumbTintList(getResources().getColorStateList(day ? R.color.colorYellow : android.R.color.transparent));
        temp_max_min.setText(weatherLocation.getMain().getTempMin().intValue() + SharedPreferenceManager.getUnitSymbol(getActivity()) + " / " + weatherLocation.getMain().getTempMax().intValue() + SharedPreferenceManager.getUnitSymbol(getActivity()));
        night.setVisibility(day ? View.GONE : View.VISIBLE);
        navView.setItemIconTintList(getResources().getColorStateList(day ? R.color.navigation_color_state : R.color.navigation_color_state_night));
        navView.setItemTextColor(getResources().getColorStateList(day ? R.color.navigation_color_state : R.color.navigation_color_state_night));
        switch (weatherLocation.getWeather().get(0).getIcon().substring(0, 2)) {
            case "01":
                background.setImageResource(R.drawable.clear_sky_gradient);
                icon.setImageResource(day ? R.drawable.ic_clear_sky : R.drawable.ic_clear_sky_night);
                color = R.color.colorClearSky;
                break;
            case "02":
                background.setImageResource(R.drawable.few_clouds_gradient);
                icon.setImageResource(day ? R.drawable.ic_few_clouds : R.drawable.ic_few_clouds_night);
                color = R.color.colorFewClouds;
                break;
            case "03":
                background.setImageResource(R.drawable.scattered_clouds_gradient);
                icon.setImageResource(day ? R.drawable.ic_scattered_clouds : R.drawable.ic_scattered_clouds_night);
                color = R.color.colorScatteredClouds;
                break;
            case "04":
                background.setImageResource(R.drawable.broken_clouds_gradient);
                icon.setImageResource(day ? R.drawable.ic_broken_clouds : R.drawable.ic_broken_clouds_night);
                color = R.color.colorBrokenClouds;
                break;
            case "09":
                background.setImageResource(R.drawable.shower_rain_gradient);
                icon.setImageResource(day ? R.drawable.ic_shower_rain : R.drawable.ic_shower_rain_night);
                color = R.color.colorShowerRain;
                break;
            case "10":
                background.setImageResource(R.drawable.rain_gradient);
                icon.setImageResource(day ? R.drawable.ic_rain : R.drawable.ic_rain_night);
                color = R.color.colorRain;
                break;
            case "11":
                background.setImageResource(R.drawable.thunderstorm_gradient);
                icon.setImageResource(day ? R.drawable.ic_thunderstorm : R.drawable.ic_thunderstorm_night);
                color = R.color.colorThunderstorm;
                break;
            case "13":
                background.setImageResource(R.drawable.snow_gradient);
                icon.setImageResource(day ? R.drawable.ic_snow : R.drawable.ic_snow_night);
                color = R.color.colorSnow;
                break;
            case "50":
                background.setImageResource(R.drawable.mist_gradient);
                icon.setImageResource(day ? R.drawable.ic_mist : R.drawable.ic_mist_night);
                color = R.color.colorMist;
                break;
        }

        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), color));
    }


    /**
     * Set progress of seekBar when time changed
     * It works each minute
     */
    private final BroadcastReceiver timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_TICK) || action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                seekBar.setProgress(seekBar.getProgress() + 1);
            }
        }
    };
}