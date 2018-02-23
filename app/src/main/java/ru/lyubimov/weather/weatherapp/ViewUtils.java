package ru.lyubimov.weather.weatherapp;

import android.content.res.Resources;
import android.widget.TextView;

import java.util.Date;

import ru.lyubimov.weather.weatherapp.model.Weather;

/**
 * Created by Alex on 23.02.2018.
 */

public class ViewUtils {
    private static final String TAG = "ViewUtils";

    public static void setWindInformation(Resources resources, TextView windView, Weather.Wind wind) {
        StringBuilder inf = new StringBuilder();
        double deg = wind.getDeg();
        if ((deg >= 349 && deg < 360) || (deg > 0 && deg < 12)) {
            inf.append(resources.getString(R.string.n));
        } else if (deg >= 12 && deg < 79) {
            inf.append(resources.getString(R.string.ne));
        } else if (deg >= 79 && deg < 102) {
            inf.append(resources.getString(R.string.e));
        } else if (deg >= 102 && deg < 169) {
            inf.append(resources.getString(R.string.se));
        } else if (deg >= 169 && deg < 192) {
            inf.append(resources.getString(R.string.s));
        } else if (deg >= 192 && deg < 259) {
            inf.append(resources.getString(R.string.sw));
        } else if (deg >= 259 && deg < 282) {
            inf.append(resources.getString(R.string.w));
        } else if (deg >= 282 && deg < 349) {
            inf.append(resources.getString(R.string.nw));
        }
        inf.append(" ")
                .append(String.format(resources.getConfiguration().locale,"%.1f", wind.getSpeed()))
                .append(" ")
                .append(resources.getString(R.string.wind_speed));
        windView.setText(inf.toString());
    }

    public static void setCloudsInformation(TextView cloudsView, Weather.Clouds clouds) {
        StringBuilder inf = new StringBuilder();
        inf.append(clouds.getCloudPercent()).append("%");
        cloudsView.setText(inf.toString());
    }

    public static void setTimeStamp(TextView timeView, long dateStampInSeconds) {
        long timeInMills = dateStampInSeconds * 1000L; //время передается в секундах, переводим их в миллисекунды
        Date date = new Date(timeInMills);
        String time = String.format("%1$tH:%1$tM", date);
        timeView.setText(time);
    }
}
