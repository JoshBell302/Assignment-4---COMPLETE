package com.example.android.sqliteweather.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FiveDayForecast {
    @SerializedName("list")
    private ArrayList<ForecastData> forecastDataList;

    @SerializedName("city")
    private ForecastCity forecastCity;

    public FiveDayForecast() {
        this.forecastDataList = null;
        this.forecastCity = null;
    }

    public ArrayList<ForecastData> getForecastDataList() {
        return forecastDataList;
    }

    public ForecastCity getForecastCity() {
        return forecastCity;
    }
}
