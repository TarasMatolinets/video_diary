package com.mti.videodiary.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.crashlytics.android.Crashlytics;
import com.mti.videodiary.data.manager.DataBaseManager;
import com.mti.videodiary.utils.VideoDairySharePreferences;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import io.fabric.sdk.android.Fabric;
import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 09.11.14.
 */
public class VideoDiaryApplication extends Application {
    public static final String TAG = "com.mti.video_diary";
    public static final int MAX_CACHE_SIZE = 50 * 1024 * 1024;

    @Override
    public void onCreate() {
        super.onCreate();

        DataBaseManager.init(this);
        Fabric.with(this, new Crashlytics());
        initImageLoader(getApplicationContext());
        VideoDairySharePreferences.setContext(getApplicationContext());
    }

    private void initImageLoader(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_videocam_white)
                .showImageOnFail(R.drawable.ic_videocam_white)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .diskCacheSize(MAX_CACHE_SIZE) // 50 Mb
                .build();

        ImageLoader.getInstance().init(config);
    }

}
