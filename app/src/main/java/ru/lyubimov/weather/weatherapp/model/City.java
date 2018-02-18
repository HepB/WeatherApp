package ru.lyubimov.weather.weatherapp.model;

import com.google.gson.annotations.SerializedName;

public class City {
    @SerializedName("name")
    private String cityName;
    @SerializedName("county")
    private String country;
    @SerializedName("coord")
    private Coordinate coordinate;

    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public class Coordinate {
        @SerializedName("lat")
        private float latitude;
        @SerializedName("lon")
        private float longitude;

        public float getLatitude() {
            return latitude;
        }
        public void setLatitude(float latitude) {
            this.latitude = latitude;
        }
        public float getLongitude() {
            return longitude;
        }
        public void setLongitude(float longitude) {
            this.longitude = longitude;
        }
    }
}
