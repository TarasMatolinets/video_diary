package com.mti.videodiary.data.action;

import android.content.Context;

import javax.inject.Inject;

import rx.Observable;
import storage.VideoDairyAction;

/**
 * Created by Terry on 11/22/2016.
 */

public class VideoDairyActionImpl implements VideoDairyAction {
    private Context mContext;

    @Inject
    public VideoDairyActionImpl(Context context) {
        mContext = context;
    }

    @Override
    public Observable<String> getSavedImagePath(String path) {
        VideoDairyActionFactory factory = new VideoDairyActionFactory();
        return factory.getSavedImagePath(mContext, path);
    }
}
