package com.catr.test.vrapp;

import android.os.Environment;
import android.os.StrictMode;

/**
 * Created by Wony on 2016/8/11.
 */
public class Config {
    public static final int SDCARD_VERSION = 1;
    public static final int ASSETS_VERSION = 2;

    private static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String PANO_PATH = "/pano/";
    private static final String AUDIO_PATH = "/audio/";
    private static final String VIDEO_PATH = "/video/";


    public static int versionFlag = ASSETS_VERSION;

    public static String vrPath = SDCARD_PATH + "/Caict_VR/MTI/";
    public static String panoPath = vrPath + PANO_PATH;
    public static String audioPath = vrPath + AUDIO_PATH;
    public static String videoPath = vrPath + VIDEO_PATH;

}
