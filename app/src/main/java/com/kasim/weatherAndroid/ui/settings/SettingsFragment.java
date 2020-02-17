package com.kasim.weatherAndroid.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kasim.weatherAndroid.Helpers.SharedPreferenceManager;
import com.kasim.weatherAndroid.R;

import java.util.Arrays;
import java.util.Date;

public class SettingsFragment extends Fragment implements ChipGroup.OnCheckedChangeListener {
    private SettingsViewModel settingsViewModel;
    private ChipGroup unitChipGroup, langChipGroup;
    private Chip celsius, fahrenheit, kelvin;
    private int langArray[] = {R.id.lang, R.id.lang1, R.id.lang10, R.id.lang11, R.id.lang12, R.id.lang13, R.id.lang14, R.id.lang15, R.id.lang16, R.id.lang17, R.id.lang18, R.id.lang19, R.id.lang2, R.id.lang3, R.id.lang4, R.id.lang5, R.id.lang6, R.id.lang7, R.id.lang8, R.id.lang9};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        final LinearLayout language = root.findViewById(R.id.language);
        celsius = root.findViewById(R.id.celsius);
        fahrenheit = root.findViewById(R.id.fahrenheit);
        kelvin = root.findViewById(R.id.kelvin);
        unitChipGroup = root.findViewById(R.id.unitChipGroup);
        langChipGroup = root.findViewById(R.id.langChipGroup);

        unitChipGroup.setOnCheckedChangeListener(this);
        langChipGroup.setOnCheckedChangeListener(this);

        return root;
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        int index = -1;
        for (int item : langArray) {
            index++;
            Chip chip = root.findViewById(item);
            chip.setText(getResources().getStringArray(R.array.Language)[index]);
        }

        settingsViewModel.init(getActivity());
        settingsViewModel.getLanguage().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                int index = Arrays.binarySearch(getResources().getStringArray(R.array.Language_Code), s);
                if (index > -1) {
                    Chip chip = root.findViewById(langArray[index]);
                    chip.setChecked(true);
                }
            }
        });
        settingsViewModel.getUnits().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s) {
                    case "metric":
                        celsius.setChecked(true);
                        break;
                    case "imperial":
                        fahrenheit.setChecked(true);
                        break;
                    case "default":
                        kelvin.setChecked(true);
                        ;
                        break;
                }
            }
        });
    }


    @Override
    public void onCheckedChanged(ChipGroup group, int checkedId) {
        if (group.getId() == R.id.unitChipGroup) {
            switch (checkedId) {
                case R.id.celsius:
                    SharedPreferenceManager.setUnit(getActivity(), getResources().getStringArray(R.array.Units_Value)[0]);
                    SharedPreferenceManager.setUnitSymbol(getActivity(), getResources().getStringArray(R.array.Symblos)[0]);
                    break;
                case R.id.fahrenheit:
                    SharedPreferenceManager.setUnit(getActivity(), getResources().getStringArray(R.array.Units_Value)[1]);
                    SharedPreferenceManager.setUnitSymbol(getActivity(), getResources().getStringArray(R.array.Symblos)[1]);
                    break;
                case R.id.kelvin:
                    SharedPreferenceManager.setUnit(getActivity(), getResources().getStringArray(R.array.Units_Value)[2]);
                    SharedPreferenceManager.setUnitSymbol(getActivity(), getResources().getStringArray(R.array.Symblos)[2]);
                    break;
            }
        } else {
            int index = Arrays.binarySearch(langArray, checkedId);
            if (index > -1) {
                SharedPreferenceManager.setLanguage(getActivity(), getResources().getStringArray(R.array.Language)[index]);
                SharedPreferenceManager.setLanguageCode(getActivity(), getResources().getStringArray(R.array.Language_Code)[index]);
            }
        }
        SharedPreferenceManager.setWeatherRequestTime(getActivity(), new Date().getTime() - (5 * 60 * 1000));
        SharedPreferenceManager.setForecastRequestTime(getActivity(), new Date().getTime() - (5 * 60 * 1000));
    }
}