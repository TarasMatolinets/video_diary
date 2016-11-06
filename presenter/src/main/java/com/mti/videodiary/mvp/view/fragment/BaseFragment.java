package com.mti.videodiary.mvp.view.fragment;

import android.support.v4.app.Fragment;

import com.mti.videodiary.di.IHasComponent;

/**
 * Created by taras on 23.02.15.
 */
public class BaseFragment extends Fragment {

    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((IHasComponent<C>) getActivity()).getComponent());
    }
}
