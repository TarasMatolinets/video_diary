package com.mti.videodiary.mvp.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mti.videodiary.application.VideoDiaryApplication;
import com.mti.videodiary.di.component.ActivityComponent;
import com.mti.videodiary.di.component.VideoDiaryAppComponent;
import com.mti.videodiary.di.module.ActivityModule;

/**
 * Created by Taras Matolinets on 18.01.15.
 * Activity with main logic
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setComponent();
    }

    public abstract void setComponent();

    /**
     * Get the Main Application component for dependency injection.
     *
     * @return {@link VideoDiaryAppComponent}
     */
    protected VideoDiaryAppComponent getApplicationComponent() {
        return ((VideoDiaryApplication) getApplication()).getVideoDiaryAppComponent();
    }

    /**
     * Get the Main Application component for dependency injection.
     *
     * @return {@link VideoDiaryAppComponent}
     */
    protected ActivityComponent getActivityComponent() {
        return getApplicationComponent().plus(getActivityModule());
    }


    /**
     * Get an Activity module for dependency injection.
     *
     * @return {@link ActivityModule}
     */
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

}
