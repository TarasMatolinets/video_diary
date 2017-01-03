package com.mti.videodiary.di.module;

import android.app.Activity;

import com.mti.videodiary.di.annotation.PerFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Terry on 12/6/2016.
 */

@Module
public class FragmentModule {
    private final Activity activity;

    public FragmentModule(Activity activity) {
        this.activity = activity;
    }

    /**
     * Expose the activity to dependents in the graph.
     */
    @Provides
    @PerFragment
    Activity activity() {
        return this.activity;
    }
}
