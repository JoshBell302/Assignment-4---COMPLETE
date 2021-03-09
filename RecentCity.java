package com.example.android.sqliteweather;

public class RecentCity {
    public String cityName;
    private int timeStamp;

    public RecentCity(String city, int timeStamp) {
        cityName = city;
        this.timeStamp = timeStamp;
    }

    public String getCityName() {
        return this.cityName;
    }

    public int getTimeStamp() {
        return this.timeStamp;
    }
}
