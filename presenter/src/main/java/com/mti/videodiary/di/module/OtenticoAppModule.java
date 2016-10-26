package com.mti.videodiary.di.module;

import android.content.Context;

import com.mti.videodiary.application.VideoDiaryApplication;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
public class OtenticoAppModule {
    private final VideoDiaryApplication application;

    public OtenticoAppModule(VideoDiaryApplication app) {
        application = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }

//    @Provides
//    @Singleton
//    ThreadExecutor provideThreadExecutor(JobExecutor executor) {
//        return executor;
//    }
//
//    @Provides
//    @Singleton
//    PostExecutionThread provideUIThreadExecutor(UIThread uiThread) {
//        return uiThread;
//    }

//    @Provides
//    @Singleton
//    OtenticoCloud provideNylasCloud(OtenticoCloudFactory factory) {
//        return factory;
//    }
//
//    @Provides
//    @Singleton
//    DataBase providePlanckDataBase(OntenticoDataBaseFactory planckDataBase) {
//        return planckDataBase;
//    }
}
