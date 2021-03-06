package ru.lyubimov.weather.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.lyubimov.weather.weatherapp.data.city.CityRepository;
import ru.lyubimov.weather.weatherapp.data.city.pref.EncryptCityPrefRepository;
import ru.lyubimov.weather.weatherapp.data.image.ExternalStorageLoader;
import ru.lyubimov.weather.weatherapp.data.image.ImageLoader;
import ru.lyubimov.weather.weatherapp.data.image.InternalStorageLoader;
import ru.lyubimov.weather.weatherapp.data.weather.WeatherGetter;
import ru.lyubimov.weather.weatherapp.data.weather.database.DbWeatherManager;
import ru.lyubimov.weather.weatherapp.data.weather.network.retrofit.OpenWeatherMapRetroFetcher;
import ru.lyubimov.weather.weatherapp.model.DatabaseWeatherModel;
import ru.lyubimov.weather.weatherapp.model.ForecastWeather;
import ru.lyubimov.weather.weatherapp.model.ModelConverter;
import ru.lyubimov.weather.weatherapp.model.RequestContainer;
import ru.lyubimov.weather.weatherapp.model.Weather;
import ru.lyubimov.weather.weatherapp.recycler.WeatherAdapter;
import ru.lyubimov.weather.weatherapp.utils.MessageUtils;
import ru.lyubimov.weather.weatherapp.utils.ViewUtils;

import static ru.lyubimov.weather.weatherapp.data.city.CityRepository.CITIES;


public class WeatherActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SingleObserver<ForecastWeather>,
        ChangeCityDialogFragment.ChangeCityDialogListener {

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

    private List<Disposable> mDisposables;
    private WeatherGetter mWeatherGetter;
    private DbWeatherManager mDbWeatherManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDisposables = new ArrayList<>();

        mTemperature = findViewById(R.id.temp_text);
        mCity = findViewById(R.id.city_name);
        mWeatherDescription = findViewById(R.id.weather_description);
        mWindInformation = findViewById(R.id.wind);
        mCloudsInformation = findViewById(R.id.clouds);
        mWeatherIco = findViewById(R.id.weather_ico);
        mWeatherTimesLayout = findViewById(R.id.five_day_times_layout);
        mWeatherTimesLayout.setLayoutManager(new LinearLayoutManager(this));
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        FloatingActionButton mFloatingButton = findViewById(R.id.fab);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        View header = mNavigationView.getHeaderView(0);
        CircleImageView mCircleImageView = header.findViewById(R.id.profile_image);
        ImageLoader imageLoader = new ExternalStorageLoader(getApplicationContext());
        Disposable disposable = imageLoader.getImage(InternalStorageLoader.FILENAME).subscribe(
                success -> {
                    mCircleImageView.setImageBitmap(success);
                    Log.i(TAG, Thread.currentThread().getName());
                },
                error -> {
                    mCircleImageView.setImageDrawable(getResources().getDrawable(R.drawable.logo));
                    Log.e(TAG, "error to load picture", error);
                    showError(error);
                }
        );
        mDisposables.add(disposable);

        CardView cardView = findViewById(R.id.card_view_main);
        cardView.setCardElevation(8f);

        mWeatherGetter = new OpenWeatherMapRetroFetcher();
        mDbWeatherManager = new DbWeatherManager(getApplicationContext());
        mDbWeatherManager.open();
        mFloatingButton.setOnClickListener(listener -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String message = MessageUtils.createMessageAboutWeather(mForecastWeather);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(intent, "Послать данные о погоде"));
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Disposable disposable : mDisposables) {
            disposable.dispose();
        }
        mDbWeatherManager.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
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
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Log.i(TAG, "Got a fix: " + location);
                        RequestContainer container = new RequestContainer();
                        container.setContext(getApplicationContext());
                        container.setLocation(location);
                        subscribe(container, mWeatherGetter);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_location_detected, Toast.LENGTH_LONG).show();
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

    private void updateUI() {
        Weather currentTimeWeather = Objects.requireNonNull(mForecastWeather.getWeathers()).get(0);

        String cityName = Objects.requireNonNull(mForecastWeather.getCity()).getCityName();
        mCity.setText(cityName);

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
        container.setContext(getApplicationContext());
        switch (item.getItemId()) {
            case R.id.menu_refresh_data:
                getLocationAndFetchWeatherData();
                return true;
            case R.id.menu_fetch_city:
                DialogFragment cityDialogFragment = ChangeCityDialogFragment.newInstance();
                cityDialogFragment.show(getSupportFragmentManager(), "ChangeCityDialogFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        switch (id) {
            case R.id.about_dev:
                Snackbar.make(getWindow().getDecorView(), "Родился, живу.", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.about_city:
                String cityMessage;
                if (mForecastWeather != null) {
                    cityMessage = mForecastWeather.getCity().getCityName()
                            + ", Lat: " + mForecastWeather.getCity().getCoordinate().getLatitude()
                            + " Lon :" + mForecastWeather.getCity().getCoordinate().getLongitude();
                } else {
                    cityMessage = getString(R.string.unknown_city)
                            + ", Lat: " + getString(R.string.unknown_lat)
                            + " Lon :" + getString(R.string.unknown_lon);
                }
                Snackbar.make(getWindow().getDecorView(), cityMessage, Snackbar.LENGTH_LONG).show();
                break;
            case R.id.about_temp:
                String tempMessage;
                if (mForecastWeather != null) {
                    tempMessage = "Min: " + mForecastWeather.getWeathers().get(0).getTemperature().getTempMin() + "c, "
                            + "Max: " + mForecastWeather.getWeathers().get(0).getTemperature().getTempMax() + "c";
                } else {
                    tempMessage = "Min: " + getString(R.string.unknown_temp) + ", "
                            + "Max: " + getString(R.string.unknown_temp);
                }
                Snackbar.make(getWindow().getDecorView(), tempMessage, Snackbar.LENGTH_LONG).show();
                break;
            case R.id.encrypted_string:
                String string = getPreferences(Context.MODE_PRIVATE).getString(CITIES, "");
                Snackbar.make(getWindow().getDecorView(),
                        string, Snackbar.LENGTH_LONG)
                        .show();
                break;
            default:
                Log.d(TAG, "Wrong button id");
        }

        // закрываем NavigationView, параметр определяет анимацию закрытия
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void subscribe(RequestContainer container, WeatherGetter getter) {
        getter.getWeatherResult(container)
                .doOnSuccess(forecastWeather -> {
                    Log.i(TAG, Thread.currentThread().getName() + " doOnSuccess");
                    DatabaseWeatherModel databaseWeatherModel = ModelConverter.requestToDbModelConvert(forecastWeather);
                    mDbWeatherManager.addWeather(databaseWeatherModel);
                })
                .onErrorResumeNext(Single.create(emitter -> {
                    //SingleEmitter
                    Log.d(TAG, "network error");
                    DatabaseWeatherModel databaseWeatherModel = mDbWeatherManager.getLastWeather();
                    Log.d(TAG, databaseWeatherModel.toString());
                    Log.d(TAG, Thread.currentThread().getName() + " onErrorResumeNext");
                    showError(new Throwable(getString(R.string.db_message)));
                    emitter.onSuccess(ModelConverter.dbToRequestModelConvert(databaseWeatherModel));
                })).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposables.add(d);
    }

    @Override
    public void onSuccess(ForecastWeather weather) {
        mForecastWeather = weather;
        updateUI();
    }

    @Override
    public void onError(Throwable t) {
        showError(t);
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText editText = dialog.getDialog().findViewById(R.id.edit_city_name);
        String cityName = editText.getText().toString();
        RequestContainer container = new RequestContainer();
        container.setContext(getApplicationContext());
        container.setCityName(cityName);
        subscribe(container, mWeatherGetter);
        //по-хорошему, конечно, нужно все эти вещи создавть с помощью DI, но т. к проект учебный, будем лепить сильную связанность.
        CityRepository repository = new EncryptCityPrefRepository(getPreferences(Context.MODE_PRIVATE));
        repository.addCity(cityName);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    private void showError(Throwable error) {
        this.runOnUiThread(() -> {
            Log.e(TAG, error.getMessage());
            Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
