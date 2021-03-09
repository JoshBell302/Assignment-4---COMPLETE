package com.example.android.sqliteweather.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.Serializable;
import java.lang.reflect.Type;

@Entity(tableName = "recentSearch")
public class ForecastCity implements Serializable {
    @PrimaryKey
    @NonNull
    public String name;
    public double latitude;
    public double longitude;
    public int timezoneOffsetSeconds;
    public int timestamp;

    public ForecastCity() {
        this.name = null;
        this.latitude = 0;
        this.longitude = 0;
        this.timezoneOffsetSeconds = 0;
    }

    public ForecastCity(String name, double latitude, double longitude, int timezoneOffsetSeconds, int timestamp) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezoneOffsetSeconds = timezoneOffsetSeconds;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getTimezoneOffsetSeconds() {
        return timezoneOffsetSeconds;
    }

    public int getTimestamp() { return timestamp; }

    /**
     * This class is a custom JSON deserializer that can be plugged into Gson to directly parse
     * parts of the OpenWeather 5-day/3-hour forecast API response into a ForecastCity object.
     * Specifically, this class can be used to parse the `city` field of the OpenWeather API
     * response into a ForecastCity object.
     *
     * Using a deserializer like this allows for directly mapping deeply nested fields in the API
     * response into a single, flat object like ForecastCity instead of creating a complex Java
     * class hierarchy to mimic the structure of the API response.
     *
     * The mapping from the fields of the `city` object to the fields of a ForecastCity object are
     * as follows:
     *
     *   city.name --> ForecastCity.name
     *   city.coord.lat --> ForecastCity.latitude
     *   city.coord.lon --> ForecastCity.longitude
     *   city.timezone --> ForecastCity.timezoneOffsetSeconds
     */
    public static class JsonDeserializer implements com.google.gson.JsonDeserializer<ForecastCity> {
        @Override
        public ForecastCity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject cityObj = json.getAsJsonObject();
            JsonObject coordObj = cityObj.getAsJsonObject("coord");
            int time = 5;
            return new ForecastCity(
                    cityObj.getAsJsonPrimitive("name").getAsString(),
                    coordObj.getAsJsonPrimitive("lat").getAsDouble(),
                    coordObj.getAsJsonPrimitive("lon").getAsDouble(),
                    cityObj.getAsJsonPrimitive("timezone").getAsInt(),
                    time
            );
        }
    }
}
