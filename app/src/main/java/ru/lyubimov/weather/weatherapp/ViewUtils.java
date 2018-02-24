package ru.lyubimov.weather.weatherapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    public static void setTimeStamp(Resources resources, TextView timeView, long dateStampInSeconds) {
        long timeInMills = dateStampInSeconds * 1000L; //время передается в секундах, переводим их в миллисекунды
        Date date = new Date(timeInMills);
        String format = "%1$ta %1$tR";

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        Calendar stampDate = Calendar.getInstance();
        stampDate.setTime(date);

        if(currentDate.get(Calendar.DAY_OF_YEAR) == stampDate.get(Calendar.DAY_OF_YEAR)) {
            format = "%1$tR";
        }

        Locale locale = resources.getConfiguration().locale;
        String time = String.format(locale, format, date);
        timeView.setText(time);
    }

    public static void setWeatherIcon(Context context, ImageView imageView, String iconName) {
        Resources resources = context.getResources();
        String fullName = "_" + iconName;
        Log.i(TAG, fullName);
        final int resourceId = resources.getIdentifier(fullName, "drawable", context.getPackageName());
        imageView.setImageDrawable(resources.getDrawable(resourceId));
    }

    public static void setTemperatureInformation(Resources resources, TextView tempView, Weather.Temperature temp) {
        String temperature = String.format(resources.getConfiguration().locale, "%.0f", temp.getTemp())
                + "c" + (char) 0x00B0;
        tempView.setText(temperature);
    }
}
