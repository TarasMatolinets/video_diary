package com.mti.videodiary.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Created by Taras Matolinets on 18.01.15.
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected View onClick(Activity activity, View view, int id, View.OnClickListener listener) {

        view = activity.findViewById(id);
        view.setOnClickListener(listener);

        return view;
    }
}
