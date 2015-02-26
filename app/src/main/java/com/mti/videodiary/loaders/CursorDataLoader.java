package com.mti.videodiary.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

/**
 * Created by Taras Matolinets on 26.02.15.
 */
public class CursorDataLoader extends CursorLoader {

    public CursorDataLoader(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        return super.loadInBackground();
    }
}
