package ru.lyubimov.weather.weatherapp.data.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.lyubimov.weather.weatherapp.R;

abstract class ImageStorageLoader implements ImageLoader {
    private static final String TAG = "ExternalStorageLoader";

    Context context;

    ImageStorageLoader(Context context) {
        this.context = context;
    }

    @Override
    public Single<Bitmap> getImage(String path) {
        return Single.create((SingleOnSubscribe<Bitmap>) emitter -> getBitmap(path))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Bitmap initBitmapFromStorage(Bitmap bitmap, File file) {
        try(FileInputStream fis = new FileInputStream(file)) {
            Log.i(TAG, "OK");
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (IOException e) {
            Log.e(TAG, context.getString(R.string.load_file_error), e);
        }
        return bitmap;
    }

    void saveBitmapInStorage(Bitmap bitmap, File file) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            Log.i(TAG, "Saving file in external storage");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
        } catch (IOException ex) {
            Log.e(TAG, context.getString(R.string.save_file_error));
        }
    }

    abstract Bitmap getBitmap(String path);
}
