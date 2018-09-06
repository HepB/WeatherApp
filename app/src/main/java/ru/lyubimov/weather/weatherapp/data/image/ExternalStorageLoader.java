package ru.lyubimov.weather.weatherapp.data.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import ru.lyubimov.weather.weatherapp.R;

public class ExternalStorageLoader extends ImageStorageLoader {
    private static final String TAG = "ExternalStorageLoader";

    public static final String FILENAME = "logo.png";

    public ExternalStorageLoader(Context context) {
        super(context);
    }

    @Override
    Bitmap getBitmap(String path) {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DCIM), path);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        Log.i(TAG, Thread.currentThread().getName());
        if (!isExternalStorageReadable()) {
            Log.e(TAG, context.getString(R.string.not_readable_external_storage));
            return bitmap;
        }
        if (!file.exists()) {
            //создадим фаил из ресурсов и запишем его во внутреннюю область памяти
            if (!isExternalStorageWritable()) {
                Log.e(TAG, context.getString(R.string.not_writable_external_storage));
                return bitmap;
            }
            saveBitmapInStorage(bitmap, file);
        } else {
            bitmap = initBitmapFromStorage(bitmap, file);
        }
        return bitmap;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}
