package com.example.android.sqliteweather.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RecentSearchesRepository {
    private RecentSearchesDao dao;

    public RecentSearchesRepository(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);
        this.dao = db.recentSearchesDao();
    }

    public void insertRecentSearch(ForecastCity forecastCity) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insert(forecastCity);
            }
        });
    }

    public void deleteRecentSearch(ForecastCity forecastCity) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.delete(forecastCity);
            }
        });
    }

    public LiveData<List<ForecastCity>> getAllRecentSearches() {
        return this.dao.getLocations();
    }
}
