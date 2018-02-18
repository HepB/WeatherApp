package ru.lyubimov.weather.weatherapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    @SerializedName("dt")
    private long dateStamp;
    @SerializedName("main")
    private Temperature temperature;
    @SerializedName("weather")
    private List<Condition> conditions;
    @SerializedName("clouds")
    private Clouds clouds;
    @SerializedName("wind")
    private Wind wind;

    public long getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(long dateStamp) {
        this.dateStamp = dateStamp;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    //в json блок weather передается в виде массива, будем брать певый элемент и доставать данные из него.
    public Condition getCondition() {
        return conditions != null ? conditions.get(0) : null;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public class Temperature {
        @SerializedName("temp")
        private float temp;
        @SerializedName("temp_min")
        private float tempMin;
        @SerializedName("temp_max")
        private float tempMax;

        public float getTemp() {
            return temp;
        }

        public void setTemp(float temp) {
            this.temp = temp;
        }

        public float getTempMin() {
            return tempMin;
        }

        public void setTempMin(float tempMin) {
            this.tempMin = tempMin;
        }

        public float getTempMax() {
            return tempMax;
        }

        public void setTempMax(float tempMax) {
            this.tempMax = tempMax;
        }
    }

    public class Condition {
        @SerializedName("description")
        private String description;
        @SerializedName("icon")
        private String iconName;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIconName() {
            return iconName;
        }

        public void setIconName(String iconName) {
            this.iconName = iconName;
        }
    }

    public class Wind {
        @SerializedName("speed")
        private float speed;
        @SerializedName("deg")
        private float deg;

        public float getSpeed() {
            return speed;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }

        public float getDeg() {
            return deg;
        }

        public void setDeg(float deg) {
            this.deg = deg;
        }
    }

    public class Clouds {
        @SerializedName("all")
        private int cloudPercent;

        public int getCloudPercent() {
            return cloudPercent;
        }

        public void setCloudPercent(int cloudPercent) {
            this.cloudPercent = cloudPercent;
        }
    }
}