package com.example.android.sqliteweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sqliteweather.data.FiveDayForecast;
import com.example.android.sqliteweather.data.ForecastCity;
import com.example.android.sqliteweather.data.ForecastData;
import com.example.android.sqliteweather.data.LoadingStatus;
import com.example.android.sqliteweather.data.OpenWeatherService;
import com.example.android.sqliteweather.data.RecentSearchesRepository;
import com.google.android.material.navigation.NavigationView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity
        implements ForecastAdapter.OnForecastItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    /*
     * To use your own OpenWeather API key, create a file called `gradle.properties` in your
     * GRADLE_USER_HOME directory (this will usually be `$HOME/.gradle/` in MacOS/Linux and
     * `$USER_HOME/.gradle/` in Windows), and add the following line:
     *
     *   OPENWEATHER_API_KEY="<put_your_own_OpenWeather_API_key_here>"
     *
     * The Gradle build for this project is configured to automatically grab that value and store
     * it in the field `BuildConfig.OPENWEATHER_API_KEY` that's used below.  You can read more
     * about this setup on the following pages:
     *
     *   https://developer.android.com/studio/build/gradle-tips#share-custom-fields-and-resource-values-with-your-app-code
     *
     *   https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties
     *
     * Alternatively, you can just hard-code your API key below 🤷‍.  If you do hard code your API
     * key below, make sure to get rid of the following line (line 18) in build.gradle:
     *
     *   buildConfigField("String", "OPENWEATHER_API_KEY", OPENWEATHER_API_KEY)
     */
    private static final String OPENWEATHER_APPID = "078a4650e074b46253eb8276cf453ca2";

    private ForecastAdapter forecastAdapter;
    private FiveDayForecastViewModel fiveDayForecastViewModel;
    private RecentSearchesViewModel recentSearchesViewModel;

    private SharedPreferences sharedPreferences;

    private ForecastCity forecastCity;
    private OpenWeatherService openWeatherService;

    private RecyclerView forecastListRV;
    private RecyclerView cityListRV;
    private CityAdapter cityListAdapter;
    private RecyclerView.LayoutManager cityListLM;
    private ProgressBar loadingIndicatorPB;
    private TextView errorMessageTV;

    private Toast errorToast;

    private DrawerLayout drawerLayout;

    private ArrayList<RecentCity> cityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.cityListRV = findViewById(R.id.rv_menu);
        this.cityListLM = new LinearLayoutManager(this);

        this.loadingIndicatorPB = findViewById(R.id.pb_loading_indicator);
        this.errorMessageTV = findViewById(R.id.tv_error_message);
        this.forecastListRV = findViewById(R.id.rv_forecast_list);
        this.forecastListRV.setLayoutManager(new LinearLayoutManager(this));
        this.forecastListRV.setHasFixedSize(true);

        this.forecastAdapter = new ForecastAdapter(this);
        this.forecastListRV.setAdapter(this.forecastAdapter);

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        this.drawerLayout = findViewById(R.id.drawer_layout);

        this.fiveDayForecastViewModel = new ViewModelProvider(this)
                .get(FiveDayForecastViewModel.class);

        this.recentSearchesViewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())
        ).get(RecentSearchesViewModel.class);

        this.recentSearchesViewModel.getAllRecentSearches().observe(
                this,
                new Observer<List<ForecastCity>>() {
                    @Override
                    public void onChanged(List<ForecastCity> forecastCities) {
                        removeAll();
                        Log.d(TAG, "Length of the Citys are: " + forecastCities.size() );

                        ArrayList<ForecastCity> sortingList = new ArrayList<>();
                        int currLength = forecastCities.size();
                        while (currLength != 0) {
                            int largeTime = 0;
                            int largePosition = 0;
                            for (int i = 0; i < currLength; i++) {
                                sortingList.add(forecastCities.get(i));
                                if (forecastCities.get(i).getTimestamp() >= largeTime) {
                                    largeTime = forecastCities.get(i).getTimestamp();
                                    largePosition = i;
                                }
                            }
                            Log.d(TAG, "Current City is called: " + forecastCities.get(largePosition).getName() + " with a timestamp of " + forecastCities.get(largePosition).getTimestamp());
                            cityList.add(new RecentCity(forecastCities.get(largePosition).getName(), forecastCities.get(largePosition).getTimestamp()));
                            forecastCities.remove(largePosition);
                            currLength--;
                        }
                        cityListAdapter = new CityAdapter(cityList);
                        cityListRV.setLayoutManager(cityListLM);
                        cityListRV.setAdapter(cityListAdapter);

                        cityListAdapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                Log.d(TAG, "Clicked City: " + cityList.get(position).getCityName());

                                fiveDayForecastViewModel.loadForecast(
                                        sharedPreferences.getString(
                                                cityList.get(position).getCityName(),
                                                cityList.get(position).getCityName()
                                        ),
                                        sharedPreferences.getString(
                                                getString(R.string.pref_units_key),
                                                getString(R.string.pref_units_default_value)
                                        ),
                                        OPENWEATHER_APPID,
                                        recentSearchesViewModel
                                );
                                cityListAdapter.notifyItemChanged(position);
                            }
                        });
                    }
                }
        );

        this.loadForecast(recentSearchesViewModel);

        /*
         * Update UI to reflect newly fetched forecast data.
         */
        this.fiveDayForecastViewModel.getFiveDayForecast().observe(
                this,
                new Observer<FiveDayForecast>() {
                    @Override
                    public void onChanged(FiveDayForecast fiveDayForecast) {
                        forecastAdapter.updateForecastData(fiveDayForecast);
                        if (fiveDayForecast != null) {
                            forecastCity = fiveDayForecast.getForecastCity();
                            ActionBar actionBar = getSupportActionBar();
                            actionBar.setTitle(forecastCity.getName());
                        }
                    }
                }
        );

        /*
         * Update UI to reflect changes in loading status.
         */
        this.fiveDayForecastViewModel.getLoadingStatus().observe(
                this,
                new Observer<LoadingStatus>() {
                    @Override
                    public void onChanged(LoadingStatus loadingStatus) {
                        if (loadingStatus == LoadingStatus.LOADING) {
                            loadingIndicatorPB.setVisibility(View.VISIBLE);
                        } else if (loadingStatus == LoadingStatus.SUCCESS) {
                            loadingIndicatorPB.setVisibility(View.INVISIBLE);
                            forecastListRV.setVisibility(View.VISIBLE);
                            errorMessageTV.setVisibility(View.INVISIBLE);
                        } else {
                            loadingIndicatorPB.setVisibility(View.INVISIBLE);
                            forecastListRV.setVisibility(View.INVISIBLE);
                            errorMessageTV.setVisibility(View.VISIBLE);
                            errorMessageTV.setText(getString(R.string.loading_error, "ヽ(。_°)ノ"));
                        }
                    }
                }
        );


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    public void removeAll() {
        int length = cityList.size();
        for (int i = 0; i < length; i++) {
            cityList.remove(0);
        }
    }

    @Override
    public void onForecastItemClick(ForecastData forecastData) {
        Intent intent = new Intent(this, ForecastDetailActivity.class);
        intent.putExtra(ForecastDetailActivity.EXTRA_FORECAST_DATA, forecastData);
        intent.putExtra(ForecastDetailActivity.EXTRA_FORECAST_CITY, this.forecastCity);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                viewForecastCityInMap();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                this.drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        this.loadForecast(recentSearchesViewModel);
    }

    /**
     * Triggers a new forecast to be fetched based on current preference values.
     */
    private void loadForecast(RecentSearchesViewModel rsvm) {
        this.fiveDayForecastViewModel.loadForecast(
                this.sharedPreferences.getString(
                        getString(R.string.pref_location_key),
                        "Corvallis,OR,US"
                ),
                this.sharedPreferences.getString(
                        getString(R.string.pref_units_key),
                        getString(R.string.pref_units_default_value)
                ),
                OPENWEATHER_APPID,
                rsvm
        );
    }

    /**
     * This function uses an implicit intent to view the forecast city in a map.
     */
    private void viewForecastCityInMap() {
        if (this.forecastCity != null) {
            Uri forecastCityGeoUri = Uri.parse(getString(
                    R.string.geo_uri,
                    this.forecastCity.getLatitude(),
                    this.forecastCity.getLongitude(),
                    12
            ));
            Intent intent = new Intent(Intent.ACTION_VIEW, forecastCityGeoUri);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                if (this.errorToast != null) {
                    this.errorToast.cancel();
                }
                this.errorToast = Toast.makeText(
                        this,
                        getString(R.string.action_map_error),
                        Toast.LENGTH_LONG
                );
                this.errorToast.show();
            }
        }
    }
}