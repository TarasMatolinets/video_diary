package com.mti.videodiary.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Taras Matolinets on 21.03.15.
 */
@Singleton
public class VideoDairySharePreferences {
    private static final String VIDEO_DAIRY_SHARED_PREFERENCES = "com.video.dairy.preferences";
    private Context mContext;

    @Inject
    public VideoDairySharePreferences(Context context) {
        mContext = context;
    }

    public SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(VIDEO_DAIRY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setDataToSharePreferences(String key, Object value, SHARE_PREFERENCES_TYPE type) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();

        switch (type) {
            case STRING:
                String valueString = (String) value;
                editor.putString(key, valueString);
                break;

            case INTEGER:
                Integer valueInteger = (Integer) value;
                editor.putInt(key, valueInteger);
                break;
        }

        editor.apply();
    }

    public enum SHARE_PREFERENCES_TYPE {
        STRING, INTEGER
    }
}
