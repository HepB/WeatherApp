package ru.lyubimov.weather.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Objects;

import ru.lyubimov.weather.weatherapp.fetcher.FetchByCity;
import ru.lyubimov.weather.weatherapp.fetcher.FetcherByGeo;
import ru.lyubimov.weather.weatherapp.fetcher.WeatherFetcher;
import ru.lyubimov.weather.weatherapp.model.AsyncTaskResult;
import ru.lyubimov.weather.weatherapp.model.ForecastWeather;
import ru.lyubimov.weather.weatherapp.model.RequestContainer;
import ru.lyubimov.weather.weatherapp.model.Weather;
import ru.lyubimov.weather.weatherapp.recycler.WeatherAdapter;

/**
 * Created by Alex on 13.12.2017.
 */

public class WeatherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "WeatherActivity";

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
    private RecyclerView mWeatherTimesLayout;
    private DrawerLayout mDrawer;

    private WeatherFetcher mWeatherFetcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTemperature = findViewById(R.id.temp_text);
        mCity = findViewById(R.id.city_name);
        mWeatherDescription = findViewById(R.id.weather_description);
        mWindInformation = findViewById(R.id.wind);
        mCloudsInformation = findViewById(R.id.clouds);
        mWeatherIco = findViewById(R.id.weather_ico);
        mWeatherTimesLayout = findViewById(R.id.five_day_times_layout);
        mWeatherTimesLayout.setLayoutManager(new LinearLayoutManager(this));
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(this,LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        } else {
            getLocationAndFetchWeatherData();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocationAndFetchWeatherData() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.i(TAG, "Got a fix: " + location);
                            RequestContainer container = new RequestContainer();
                            container.setResources(getResources());
                            container.setLocation(location);
                            mWeatherFetcher = new FetcherByGeo();
                            new FetchWeatherTask().execute(container);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.no_location_detected, Toast.LENGTH_LONG).show();
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
                    getLocationAndFetchWeatherData();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.permission_denied_explanation), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean hasLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchWeatherTask extends AsyncTask<RequestContainer, Void, AsyncTaskResult<ForecastWeather>> {

        @Override
        protected AsyncTaskResult<ForecastWeather> doInBackground(RequestContainer... containers) {
            try {
                ForecastWeather forecastWeather = mWeatherFetcher.downloadWeather(containers[0]);
                return new AsyncTaskResult<>(forecastWeather);
            } catch (RuntimeException ex) {
                return new AsyncTaskResult<>(ex);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<ForecastWeather> result) {
            if (result.getError() != null) {
                Toast.makeText(getApplicationContext(), result.getError().getMessage(), Toast.LENGTH_LONG).show();
            } else {
                mForecastWeather = result.getResult();
                updateUI();
            }
        }
    }

    private void updateUI() {
        Weather currentTimeWeather = Objects.requireNonNull(mForecastWeather.getWeathers()).get(0);

        String cityName = Objects.requireNonNull(mForecastWeather.getCity()).getCityName();
        mCity.setText(cityName);
        addPopup();

        String weatherDescription = Objects.requireNonNull(currentTimeWeather.getConditions()).get(0).getDescription();
        mWeatherDescription.setText(weatherDescription);

        ViewUtils.setTemperatureInformation(getResources(), mTemperature, Objects.requireNonNull(currentTimeWeather.getTemperature()));
        ViewUtils.setWindInformation(getResources(), mWindInformation, Objects.requireNonNull(currentTimeWeather.getWind()));
        ViewUtils.setCloudsInformation(mCloudsInformation, Objects.requireNonNull(currentTimeWeather.getClouds()));
        ViewUtils.setWeatherIcon(Objects.requireNonNull(getApplicationContext()), mWeatherIco, currentTimeWeather.getConditions().get(0).getIconName());

        setupWeathersView();
    }

    /**
     * Формирование отображения погоды по временным отрезкам на 5 дней.
     */
    private void setupWeathersView() {
        mWeatherTimesLayout.removeAllViews();
        List<Weather> weathers = mForecastWeather.getWeathers();
        WeatherAdapter mWeatherAdapter = new WeatherAdapter(weathers, this);
        mWeatherTimesLayout.setAdapter(mWeatherAdapter);
    }

    //Домашнее задание к первому уроку
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RequestContainer container = new RequestContainer();
        container.setResources(getResources());
        switch (item.getItemId()) {
            case R.id.menu_refresh_data:
                getLocationAndFetchWeatherData();
                return true;
            case R.id.menu_fetch_msk:
                container.setCityName("Moscow,RU");
                mWeatherFetcher = new FetchByCity();
                new FetchWeatherTask().execute(container);
                return true;
            case R.id.menu_fetch_lnd:
                container.setCityName("London,UK");
                mWeatherFetcher = new FetchByCity();
                new FetchWeatherTask().execute(container);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addPopup() {
        mCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        RequestContainer container = new RequestContainer();
                        container.setResources(getResources());
                        switch (item.getItemId()) {
                            case R.id.menu_fetch_msk:
                                container.setCityName("Moscow,RU");
                                mWeatherFetcher = new FetchByCity();
                                new FetchWeatherTask().execute(container);
                                return true;
                            case R.id.menu_fetch_lnd:
                                container.setCityName("London,UK");
                                mWeatherFetcher = new FetchByCity();
                                new FetchWeatherTask().execute(container);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START))
            mDrawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here
        int id = item.getItemId();

        if (id == R.id.about_dev) {
            Snackbar.make(getWindow().getDecorView(), "Родился, живу.", Snackbar.LENGTH_LONG)
                   .show();
        } else if (id == R.id.about_city) {
            Snackbar.make(getWindow().getDecorView(), mForecastWeather.getCity().getCityName() + ", Lat: " +
                     mForecastWeather.getCity().getCoordinate().getLatitude() +
                    " Lon :" + mForecastWeather.getCity().getCoordinate().getLatitude(), Snackbar.LENGTH_LONG)
                    .show();
        } else if (id == R.id.about_temp) {
            Snackbar.make(getWindow().getDecorView(),
                    "Min: " + mForecastWeather.getWeathers().get(0).getTemperature().getTempMin() + "c, "
                            + "Max: " + mForecastWeather.getWeathers().get(0).getTemperature().getTempMax() + "c", Snackbar.LENGTH_LONG)
                    .show();
        }

        // закрываем NavigationView, параметр определяет анимацию закрытия
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
