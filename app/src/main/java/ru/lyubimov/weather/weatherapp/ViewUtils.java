package ru.lyubimov.weather.weatherapp;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.lyubimov.weather.weatherapp.model.Weather;

/**
 * Класс утилит для централизованной работы с View.
 */

class ViewUtils {
    private static final String TAG = "ViewUtils";

    /**
     * Метод для формирования во view данных о ветре
     * @param resources ресурсы приложения. В них содерижтся информация о направлении ветра.
     * @param windView view, отображающая данные о ветре.
     * @param wind данные о ветре, полученные из внешнего api
     */
    static void setWindInformation(Resources resources, TextView windView, Weather.Wind wind) {
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

    /**
     * Метод для формирования во view данных об облаках (облачность в %)
     * @param cloudsView view отображающая данные об облаках
     * @param clouds данные об облачности, полученные из внешнего api
     */
    static void setCloudsInformation(TextView cloudsView, Weather.Clouds clouds) {
        String inf = clouds.getCloudPercent() + "%";
        cloudsView.setText(inf);
    }

    /**
     * Метод для формирования во view данных о dateStamp из api
     * @param resources ресурсы приложения, необходимы для получения local
     * @param timeView view отображающая данные об облаках
     * @param dateStampInSeconds значение dateStamp из api
     */
    static void setTimeStamp(Resources resources, TextView timeView, long dateStampInSeconds) {
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

    /**
     * Метод для поиска иконки погоды в ресурсах по имени, получаемому из api. Можно было бы получать
     * иконки от openweathermap.org вторым запросом, однако, слишком много запросов не очень хорошо.
     * @param context контекст приложения, необходим для поиска id иконки по имени.
     * @param imageView view отображающая иконку
     * @param iconName имя иконки получаемое из
     */
    static void setWeatherIcon(Context context, ImageView imageView, String iconName) {
        Resources resources = context.getResources();
        String fullName = "_" + iconName;
        Log.i(TAG, fullName);
        final int resourceId = resources.getIdentifier(fullName, "drawable", context.getPackageName());
        imageView.setImageDrawable(resources.getDrawable(resourceId));
    }

    /**
     * Метод для формирования во view данных о температуре из api. Метод можно было бы улучшить, в зависимости
     * от локали выставлять данные в c или f(в свою очередь формируя запрос необходимым образом). Однако, в рамках
     * ограниченных сроков, оставим данную роработку на будущее.
     * @param resources ресурсы приложения, необходимо для получения local
     * @param tempView view отображающая данные об облаках
     * @param temp
     */
    static void setTemperatureInformation(Resources resources, TextView tempView, Weather.Temperature temp) {
        String temperature = String.format(resources.getConfiguration().locale, "%.0f", temp.getTemp())
                + "c" + (char) 0x00B0;
        tempView.setText(temperature);
    }
}
