package com.mti.videodiary.di.component;

import com.mti.videodiary.di.annotation.PerActivity;
import com.mti.videodiary.di.module.ActivityModule;
//import com.mti.videodiary.mvp.view.activity.CreateNoteActivity;
//import com.mti.videodiary.mvp.view.activity.CreateVideoNoteActivity;
import com.mti.videodiary.mvp.view.activity.MenuActivity;
import com.mti.videodiary.mvp.view.activity.SplashActivity;
//import com.mti.videodiary.mvp.view.fragment.NoteFragment;
//import com.mti.videodiary.mvp.view.fragment.VideoFragment;

import dagger.Subcomponent;

/**
 * Created by Terry on 11/5/2016.
 * A base component upon which fragment's components may depend.
 * Activity-level components should extend this component.
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(SplashActivity activity);

//    void inject(CreateNoteActivity activity);
//
//    void inject(CreateVideoNoteActivity activity);

    void inject(MenuActivity activity);

//    void inject(NoteFragment fragment);
//
//    void inject(VideoFragment fragment);

}
