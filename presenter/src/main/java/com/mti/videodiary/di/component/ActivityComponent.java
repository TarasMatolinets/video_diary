package com.mti.videodiary.di.component;

import com.mti.videodiary.di.annotation.PerActivity;
import com.mti.videodiary.di.module.ActivityModule;
import com.mti.videodiary.di.module.FragmentModule;
import com.mti.videodiary.mvp.view.activity.CreateNoteActivity;
import com.mti.videodiary.mvp.view.activity.CreateVideoNoteActivity;
import com.mti.videodiary.mvp.view.activity.MenuActivity;
import com.mti.videodiary.mvp.view.activity.SplashActivity;

import dagger.Subcomponent;

//import com.mti.videodiary.mvp.view.activity.CreateVideoNoteActivity;
//import com.mti.videodiary.mvp.view.fragment.VideoFragment;

/**
 * Created by Terry on 11/5/2016.
 * A base component upon which fragment's components may depend.
 * Activity-level components should extend this component.
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    FragmentComponent plus(FragmentModule module);

    void inject(SplashActivity activity);

    void inject(MenuActivity activity);

    void inject(CreateNoteActivity activity);

    void inject(CreateVideoNoteActivity activity);
//    void inject(VideoFragment fragment);

}
