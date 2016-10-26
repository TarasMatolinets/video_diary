package com.mti.videodiary.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Taras Matolinets on 21.03.15.
 */
public class VideoDairySharePreferences {

    private static final String VIDEO_DAIRY_SHARED_PREFERENCES = "com.video.dairy.preferences";
    private static Context mContext;

    public static SharedPreferences getSharedPreferences() {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(VIDEO_DAIRY_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        return sharedpreferences;
    }

    public static void setDataToSharePreferences(String key, Object value, SHARE_PREFERENCES_TYPE type) {
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

    public static void setContext(Context context) {
        mContext = context;
    }


    public enum SHARE_PREFERENCES_TYPE {
        STRING, INTEGER;
    }
}
