package com.mti.videodiary.mvp.view.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by taras on 23.02.15.
 */
public class BaseFragment extends Fragment {
    public static final int DURATION = 1500;

    /*backPress interface*/
    public interface OnBackPress
    {
        void onBackPress();
    }
}
