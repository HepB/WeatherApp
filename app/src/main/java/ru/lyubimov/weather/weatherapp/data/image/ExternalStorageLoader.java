package ru.lyubimov.weather.weatherapp.data.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.lyubimov.weather.weatherapp.R;

public class ExternalStorageLoader implements ImageLoader {
    private static final String TAG = "ExternalStorageLoader";

    public static final String FILENAME = "logo.jpeg";

    private Context context;

    public ExternalStorageLoader(Context context) {
        this.context = context;
    }

    @Override
    public Single<Bitmap> getImage(String path){
        return Single.just(getBitmap(path))
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    private Bitmap getBitmap(String path){
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DCIM), path);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        if (!isExternalStorageReadable()) {
            Log.e(TAG, context.getString(R.string.not_readable_external_storage));
            return bitmap;
        }
        if (!file.exists()) {
            //создадим фаил из ресурсов и запишем его во внутреннюю область памяти
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
            if(!isExternalStorageWritable()) {
                Log.e(TAG, context.getString(R.string.not_writable_external_storage));
                return bitmap;
            }
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                Log.i(TAG, "Saving file in external storage");
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
            } catch (IOException ex) {
                Log.e(TAG, context.getString(R.string.save_file_error));
                return bitmap;
            }
        } else {
            try(FileInputStream fis = new FileInputStream(file)) {
                bitmap = BitmapFactory.decodeStream(fis);
                Log.i(TAG, "OK");
            } catch (IOException ex) {
                Log.e(TAG, context.getString(R.string.load_file_error));
                return bitmap;
            }
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
