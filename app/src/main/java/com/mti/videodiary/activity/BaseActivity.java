package com.mti.videodiary.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Created by Taras Matolinets on 18.01.15.
 */
public class BaseActivity extends ActionBarActivity {

    public static final String APPLICATION_DIRECTORY = "videoDairy";
    public static final String VIDEO_DIR = "video";
    public static final String NOTE_DIR = "/note";
    public static final String IMAGE_DIR = "image";
    public static final String PACKAGE = "com.mti.videodairy";

    static float sAnimatorScale = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected View onClick(Activity activity, View view, int id, View.OnClickListener listener) {

        view = activity.findViewById(id);
        view.setOnClickListener(listener);

        return view;
    }
}
