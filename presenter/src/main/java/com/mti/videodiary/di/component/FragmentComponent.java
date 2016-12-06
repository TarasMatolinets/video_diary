package com.mti.videodiary.di.component;

import com.mti.videodiary.di.annotation.PerFragment;
import com.mti.videodiary.di.module.FragmentModule;
import com.mti.videodiary.mvp.view.fragment.NoteFragment;

import dagger.Subcomponent;

/**
 * Created by Terry on 12/6/2016.
 */

@PerFragment
@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent {
    void inject(NoteFragment fragment);
}
