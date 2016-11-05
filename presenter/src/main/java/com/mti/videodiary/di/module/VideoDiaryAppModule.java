package com.mti.videodiary.di.module;

import android.content.Context;

import com.mti.videodiary.UIThread;
import com.mti.videodiary.application.VideoDiaryApplication;
import com.mti.videodiary.data.executor.JobExecutor;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import executor.PostExecutionThread;
import executor.ThreadExecutor;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
public class VideoDiaryAppModule {
    private final VideoDiaryApplication application;

    public VideoDiaryAppModule(VideoDiaryApplication app) {
        application = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    ThreadExecutor provideThreadExecutor(JobExecutor executor) {
        return executor;
    }

    @Provides
    @Singleton
    PostExecutionThread provideUIThreadExecutor(UIThread uiThread) {
        return uiThread;
    }

}
