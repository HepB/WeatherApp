package ru.lyubimov.weather.weatherapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import ru.lyubimov.weather.weatherapp.model.ForecastWeather;
import ru.lyubimov.weather.weatherapp.model.RequestContainer;
import ru.lyubimov.weather.weatherapp.model.Weather;

/**
 * Created by Alex on 17.02.2018.
 */

public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private static final int REQUEST_LOCATION_PERMISSIONS = 0;

    private FusedLocationProviderClient mFusedLocationClient;
    private ForecastWeather mForecastWeather;

    private ImageView mWeatherIco;
    private TextView mTemperature;
    private TextView mCity;
    private TextView mWeatherDescription;
    private TextView mWindInformation;
    private TextView mCloudsInformation;
    private TextView mTimeStamp;
    private LinearLayout mWeatherTimesLayout;

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment, container, false);

        mTemperature = view.findViewById(R.id.temp_text);
        mCity = view.findViewById(R.id.city_name);
        mWeatherDescription = view.findViewById(R.id.weather_description);
        mWindInformation = view.findViewById(R.id.wind);
        mCloudsInformation = view.findViewById(R.id.clouds);
        mTimeStamp = view.findViewById(R.id.date_stamp);
        mWeatherIco = view.findViewById(R.id.weather_ico);
        mWeatherTimesLayout = view.findViewById(R.id.five_day_times_layout);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!hasLocationPermission()) {
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        } else {
            getLastLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.i(TAG, "Got a fix: " + location);
                            RequestContainer container = new RequestContainer();
                            container.setLocale(getResources().getConfiguration().locale);
                            container.setLocation(location);
                            new FetchWeatherTask().execute(container);
                        }
                        else {
                            Toast.makeText(getContext(), R.string.no_location_detected, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, Integer.toString(requestCode));
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (hasLocationPermission()) {
                    getLastLocation();
                } else {
                    Toast.makeText(getContext(), getString(R.string.permission_denied_explanation), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean hasLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchWeatherTask extends AsyncTask<RequestContainer, Void, ForecastWeather> {

        @Override
        protected ForecastWeather doInBackground(RequestContainer... containers) {
            return new OpenWeatherMapFetcher().downloadWeather(containers[0]);
        }

        @Override
        protected void onPostExecute(ForecastWeather forecastWeather) {
            mForecastWeather = forecastWeather;
            updateUI();
        }
    }

    private void updateUI() {
        Weather currentTimeWeather = mForecastWeather.getWeathers().get(0);

        String cityName = mForecastWeather.getCity().getCityName();
        mCity.setText(cityName);

        String weatherDescription = currentTimeWeather.getCondition().getDescription();
        mWeatherDescription.setText(weatherDescription);

        ViewUtils.setTemperatureInformation(getResources(), mTemperature, currentTimeWeather.getTemperature());
        ViewUtils.setWindInformation(getResources(), mWindInformation, currentTimeWeather.getWind());
        ViewUtils.setCloudsInformation(mCloudsInformation, currentTimeWeather.getClouds());
        ViewUtils.setTimeStamp(getResources(), mTimeStamp, currentTimeWeather.getDateStamp());
        ViewUtils.setWeatherIcon(getContext(), mWeatherIco, currentTimeWeather.getCondition().getIconName());

        setupWeatherView();
    }

    public class WeatherAdapter extends ArrayAdapter<Weather> {
        public WeatherAdapter(Context context, ArrayList<Weather> weathers) {
            super(context, 0, weathers);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            Weather weather = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.time_stamp_item, parent, false);
            }

            TextView dateInfo = convertView.findViewById(R.id.date_info);
            ImageView weatherInfo = convertView.findViewById(R.id.weather_ico);
            TextView tempInfo = convertView.findViewById(R.id.temp_text);

            if (weather != null) {
                ViewUtils.setTimeStamp(getResources(), dateInfo, weather.getDateStamp());
                ViewUtils.setWeatherIcon(getContext(), weatherInfo, weather.getCondition().getIconName());
                ViewUtils.setTemperatureInformation(getResources(), tempInfo, weather.getTemperature());
            }
            return convertView;
        }
    }

    private void setupWeatherView() {
        mWeatherTimesLayout.removeAllViews();
        ArrayList<Weather> weathers = mForecastWeather.getWeathers();
        WeatherAdapter marketsAdapter = new WeatherAdapter(getActivity(), weathers);
        for (int i=1; i < weathers.size(); i++) {
            View vi = marketsAdapter.getView(i, null, mWeatherTimesLayout);
            mWeatherTimesLayout.addView(vi);
        }
    }
}
