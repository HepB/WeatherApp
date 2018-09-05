package ru.lyubimov.weather.weatherapp.data.image;

import android.graphics.Bitmap;
import io.reactivex.Single;

public interface ImageLoader {
    /**
     * @param path - идентификатор файла (id, путь в файловой системе, идентификатор ресуса)
     * @return Single<Bitmap>
     */
    Single<Bitmap> getImage(String path);
}
