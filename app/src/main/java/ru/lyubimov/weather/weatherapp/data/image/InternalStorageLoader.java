package ru.lyubimov.weather.weatherapp.data.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.lyubimov.weather.weatherapp.R;

public class InternalStorageLoader implements ImageLoader {
    private static final String TAG = "InternalStorageLoader";

    public static final String FILENAME = "logo.jpeg";

    private Context context;

    public InternalStorageLoader(Context context) {
        this.context = context;
    }

    @Override
    public Single<Bitmap> getImage(String path){
        return Single.just(getBitmap(path))
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    private Bitmap getBitmap(String path) {
        File file = new File(context.getFilesDir(), path);
        Bitmap bitmap = null;
        if (!file.exists()) {
            //костыльно создадим фаил из ресурсов
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
            } catch (Exception e) {
                Log.e(TAG, context.getString(R.string.save_file_error), e);
            }
        } else {
                try(FileInputStream fis = new FileInputStream(file)) {
                    bitmap = BitmapFactory.decodeStream(fis);
                    Log.i(TAG, "OK");
                } catch (Exception e) {
                    Log.e(TAG, context.getString(R.string.load_file_error), e);
                }
        }
        return bitmap;
    }
}
