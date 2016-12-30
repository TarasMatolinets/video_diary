package com.mti.videodiary.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mti.videodiary.di.component.DaggerVideoDiaryAppComponent;
import com.mti.videodiary.di.component.VideoDiaryAppComponent;
import com.mti.videodiary.di.module.VideoDiaryAppModule;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;

import io.fabric.sdk.android.Fabric;
import mti.com.videodiary.R;

import static com.mti.videodiary.data.Constants.IMAGE_DIR;
import static com.mti.videodiary.data.Constants.NOTE_DIR;
import static com.mti.videodiary.data.Constants.VIDEO_DIR;

/**
 * Created by Taras Matolinets on 09.11.14.
 * Android main application
 */
public class VideoDiaryApplication extends Application {
    public static final String TAG = "com.mti.video_diary";

    private VideoDiaryAppComponent mVideoDiaryAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        buildGraph();

        createFolder();

        Fabric.with(this, new Crashlytics());
    }

    private void createFolder() {
        String videoFolder = VIDEO_DIR;
        String noteFolder = NOTE_DIR;
        String imageDir = IMAGE_DIR;

        createFolder(videoFolder);
        createFolder(noteFolder);
        createFolder(imageDir);
    }

    private void buildGraph() {
        mVideoDiaryAppComponent = DaggerVideoDiaryAppComponent.builder()
                .videoDiaryAppModule(new VideoDiaryAppModule(this))
                .build();
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
