package ru.lyubimov.weather.weatherapp;


import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

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

    private TextView mTemperature;
    private TextView mCity;
    private TextView mWeatherDescription;
    private TextView mWindInformation;
    private TextView mCloudsInformation;
    private TextView mTimeStamp;

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
        mWeatherDescription=view.findViewById(R.id.weather_description);
        mWindInformation = view.findViewById(R.id.wind);
        mCloudsInformation = view.findViewById(R.id.clouds);
        mTimeStamp = view.findViewById(R.id.date_stamp);
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
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (hasLocationPermission()) {
                    getLastLocation();
                }
                break;
            default:
                Toast.makeText(getContext(), getString(R.string.permission_denied_explanation), Toast.LENGTH_LONG).show();
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
        String temperature = String.format(getResources().getConfiguration().locale, "%.0f",
                currentTimeWeather.getTemperature().getTemp()) + "c";
        mTemperature.setText(temperature);
        getResources().getConfiguration().locale.getCountry();

        String cityName = mForecastWeather.getCity().getCityName();
        mCity.setText(cityName);

        String weatherDescription = currentTimeWeather.getCondition().getDescription();
        mWeatherDescription.setText(weatherDescription);

        ViewUtils.setWindInformation(getResources(), mWindInformation, currentTimeWeather.getWind());
        ViewUtils.setCloudsInformation(mCloudsInformation, currentTimeWeather.getClouds());
        ViewUtils.setTimeStamp(mTimeStamp, currentTimeWeather.getDateStamp());
    }
}
