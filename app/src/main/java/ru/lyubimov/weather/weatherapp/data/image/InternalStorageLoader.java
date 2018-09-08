package ru.lyubimov.weather.weatherapp.data.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;

import ru.lyubimov.weather.weatherapp.R;

public class InternalStorageLoader extends ImageStorageLoader {
    private static final String TAG = "InternalStorageLoader";

    public static final String FILENAME = "logo.png";

    public InternalStorageLoader(Context context) {
        super(context);
    }


    @Override
    Bitmap getBitmap(String path) {
        File file = new File(context.getFilesDir(), path);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        Log.i(TAG, Thread.currentThread().getName() + " getBitmap");
        if (!file.exists()) {
            saveBitmapInStorage(bitmap, file);
        } else {
            bitmap = initBitmapFromStorage(bitmap, file);
        }
        return bitmap;
    }
}
