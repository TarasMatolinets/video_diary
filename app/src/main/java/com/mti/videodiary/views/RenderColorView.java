package com.mti.videodiary.views;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Taras Matolinets on 17.01.15.
 */
public class RenderColorView extends SurfaceView implements SurfaceHolder.Callback {
    public RenderColorView(Context context) {
        super(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
