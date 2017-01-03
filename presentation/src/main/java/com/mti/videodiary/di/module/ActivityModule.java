package com.mti.videodiary.di.module;

import android.app.Activity;

import com.mti.videodiary.di.annotation.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Module which is provide objects during activity life circles
 */
@Module
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    /**
     * Expose the activity to dependents in the graph.
     */
    @Provides
    @PerActivity
    Activity activity() {
        return this.activity;
    }

}
