package com.example.android.sqliteweather.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecentSearchesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ForecastCity forecastCity);

    @Delete
    void delete(ForecastCity forecastCity);

    @Query("SELECT * FROM recentSearch")
    LiveData<List<ForecastCity>> getLocations();
}