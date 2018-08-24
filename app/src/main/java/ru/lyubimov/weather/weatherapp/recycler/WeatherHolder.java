package ru.lyubimov.weather.weatherapp.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.lyubimov.weather.weatherapp.R;
import ru.lyubimov.weather.weatherapp.ViewUtils;
import ru.lyubimov.weather.weatherapp.model.Weather;

public class WeatherHolder extends RecyclerView.ViewHolder {
    private Weather mWeather;

    private TextView mDateInfo;
    private ImageView mWeatherInfo;
    private TextView mTempInfo;

    private Context mContext;

    WeatherHolder(LayoutInflater inflater, ViewGroup parent, Context context) {
        super(inflater.inflate(R.layout.time_stamp_item, parent, false));
        mDateInfo = itemView.findViewById(R.id.date_info);
        mWeatherInfo = itemView.findViewById(R.id.weather_ico);
        mTempInfo = itemView.findViewById(R.id.temp_text);
        mContext = context;
    }

    void bind(Weather weather) {
        mWeather = weather;

        ViewUtils.setTimeStamp(mContext.getResources(), mDateInfo, mWeather.getDateStamp());
        ViewUtils.setWeatherIcon(mContext, mWeatherInfo, mWeather.getCondition().getIconName());
        ViewUtils.setTemperatureInformation(mContext.getResources(), mTempInfo, mWeather.getTemperature());
    }
}

