package com.kasim.weatherAndroid.ui.settings;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kasim.weatherAndroid.Helpers.SharedPreferenceManager;

public class SettingsViewModel extends ViewModel {

    private MutableLiveData<String> lang;
    private MutableLiveData<String> unit;

    public void init(Context context) {
        lang = new MutableLiveData<>();
        unit = new MutableLiveData<>();
        lang.setValue(SharedPreferenceManager.getLanguageCode(context));
        unit.setValue(SharedPreferenceManager.getUnit(context));
    }

    public LiveData<String> getLanguage() {
        return lang;
    }

    public LiveData<String> getUnits() {
        return unit;
    }
}