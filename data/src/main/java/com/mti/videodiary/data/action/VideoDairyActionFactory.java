package com.mti.videodiary.data.action;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.mti.videodiary.data.helper.UserHelper;

import java.io.FileNotFoundException;
import java.io.InputStream;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Terry on 11/22/2016.
 */

public class VideoDairyActionFactory {


    public Observable<String> getSavedImagePath(final Context context, final String selectedImage) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Uri uri = Uri.parse(selectedImage);
                    InputStream is = context.getContentResolver().openInputStream(uri);
                    if (is != null) {
                        Bitmap pictureBitmap = BitmapFactory.decodeStream(is);

                        String url = UserHelper.saveBitmapToSD(pictureBitmap);
                        subscriber.onNext(url);
                        subscriber.onCompleted();
                    }
                } catch (FileNotFoundException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
