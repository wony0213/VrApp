package com.catr.test.vrapp.activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Wony on 2016/7/28.
 */
public class VrApp extends Application {
    public static final String PANORAMA_NUM = "panorama_num";
    public static final String DISPLAY_MODE = "display_mode";
    @Override
    public void onCreate() {
        super.onCreate();
    }


    public static boolean isFirstStart(Context context) {
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        //获取firstStart标志位，默认（首次）返回true。
        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
        return  isFirstStart;
    }

    public static void setFirstStart(Context context) {
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor e = getPrefs.edit();
        //把firstStart标志位设置为false
        e.putBoolean("firstStart", false);
        e.apply();
    }
}
