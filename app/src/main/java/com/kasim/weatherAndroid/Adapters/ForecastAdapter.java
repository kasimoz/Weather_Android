package com.kasim.weatherAndroid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasim.weatherAndroid.Helpers.SharedPreferenceManager;
import com.kasim.weatherAndroid.Helpers.Utils;
import com.kasim.weatherAndroid.Models.WeatherLocation;
import com.kasim.weatherAndroid.R;

import java.util.ArrayList;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    Context context;
    ArrayList<WeatherLocation> list;

    public ForecastAdapter(Context context, ArrayList<WeatherLocation> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ForecastAdapter.ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_item, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapter.ForecastViewHolder holder, int position) {
        String date[] = Utils.getDayOfDate(list.get(0).getDt_txt(), position + 1).split(" ");
        holder.day.setText(date[1]);
        int index = getIndexOfDate(date[0] + " 12:00:00");
        holder.temp.setText(list.get(index).getMain().getTemp().intValue() + SharedPreferenceManager.getUnitSymbol(context));
        holder.icon.setImageResource(Utils.getIconOfDay(list.get(index).getWeather().get(0)));
    }

    @Override
    public int getItemCount() {
        return list.size() > 0 ? 5 : 0;
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {

        TextView day;
        TextView temp;
        ImageView icon;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);

            day = itemView.findViewById(R.id.day);
            temp = itemView.findViewById(R.id.temp);
            icon = itemView.findViewById(R.id.icon);

        }
    }

    public int getIndexOfDate(String date) {
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            if (date.equals(list.get(i).getDt_txt())) {
                index = i;
                break;
            }
        }
        return index;
    }


}
