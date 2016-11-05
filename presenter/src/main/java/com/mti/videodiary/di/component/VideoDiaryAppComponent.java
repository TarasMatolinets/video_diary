package com.mti.videodiary.di.component;

import com.mti.videodiary.di.module.ActivityModule;
import com.mti.videodiary.di.module.VideoDiaryAppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Terry on 11/5/2016.
 * A component with whole lifetime is the life of the application
 */
@Singleton
@Component(modules = VideoDiaryAppModule.class)
public interface VideoDiaryAppComponent {
    ActivityComponent plus(ActivityModule module);
}
