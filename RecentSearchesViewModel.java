package com.example.android.sqliteweather;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.sqliteweather.data.FiveDayForecast;
import com.example.android.sqliteweather.data.ForecastCity;
import com.example.android.sqliteweather.data.RecentSearchesRepository;

import java.util.List;

public class RecentSearchesViewModel extends AndroidViewModel {
    private RecentSearchesRepository repo;

    public RecentSearchesViewModel(Application application) {
        super(application);
        this.repo = new RecentSearchesRepository(application);
    }

    public void insertFiveDayForecast(ForecastCity forecastCity) {
        this.repo.insertRecentSearch(forecastCity);
    }

    public void deleteFiveDayForecast(ForecastCity forecastCity) {
        this.repo.deleteRecentSearch(forecastCity);
    }

    public LiveData<List<ForecastCity>> getAllRecentSearches() {
        return this.repo.getAllRecentSearches();
    }
}
