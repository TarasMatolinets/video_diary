package com.mti.videodiary.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mti.videodiary.di.component.VideoDiaryAppComponent;
import com.mti.videodiary.di.module.VideoDiaryAppModule;
import com.mti.videodiary.mvp.view.BaseActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;

import io.fabric.sdk.android.Fabric;
import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 09.11.14.
 * Android main application
 */
public class VideoDiaryApplication extends Application {
    public static final String TAG = "com.mti.video_diary";

    private static final String APPLICATION_DIRECTORY = "videoDairy";
    private static final String VIDEO_DIR = "video";
    private static final String NOTE_DIR = "/note";
    private static final String IMAGE_DIR = "image";

    public static final int MAX_CACHE_SIZE = 50 * 1024 * 1024;
    private VideoDiaryAppComponent mVideoDiaryAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        buildGraph();

        createFolder();

        Fabric.with(this, new Crashlytics());
        initImageLoader(getApplicationContext());

    }

    private void createFolder() {
        String videoFolder = File.separator + APPLICATION_DIRECTORY + File.separator + VIDEO_DIR;
        String noteFolder = File.separator + APPLICATION_DIRECTORY + File.separator + NOTE_DIR;
        String imageDir = File.separator + APPLICATION_DIRECTORY + File.separator + IMAGE_DIR;

        createFolder(videoFolder);
        createFolder(noteFolder);
        createFolder(imageDir);
    }

    private void buildGraph() {
        mVideoDiaryAppComponent = DaggerVideoDiaryAppComponent.builder()
                .videoDiaryAppModule(new VideoDiaryAppModule(this))
                .build();
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

    public VideoDiaryAppComponent getVideoDiaryAppComponent() {
        return mVideoDiaryAppComponent;
    }

    /**
     * create folders for feature files
     *
     * @param nameFolder folder name
     */
    private void createFolder(String nameFolder) {
        File f = new File(Environment.getExternalStorageDirectory(), nameFolder);

        if (!f.exists()) {
            boolean isCreated = f.mkdirs();

            if (isCreated) {
                Log.i(TAG, "folder " + nameFolder + " created");
            }
        }
    }

}
