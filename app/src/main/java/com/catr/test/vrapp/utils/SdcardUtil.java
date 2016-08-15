package com.catr.test.vrapp.utils;

import android.os.Environment;

import com.catr.test.vrapp.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wony on 2016/8/11.
 */
public class SdcardUtil {

    //判断是否有SD卡
    public static boolean isSdCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

//    public static List<String> getSdcardFileNames() {
//        if (isSdCardMounted()) {
//            File sdFolder = new File(Environment.getExternalStorageDirectory().getPath());
//            File[] sdFiles = sdFolder.listFiles();
//            return  null;
//        }
//        return null;
//    }

    public static List<String> getPanoNames() {
        if (isSdCardMounted()) {
            File panoFolder = new File(Config.panoPath);
            File[] panoFiles = panoFolder.listFiles();
            List<String> panoNames = new ArrayList<>();
            for (int i = 0; i < panoFiles.length; i++) {
                panoNames.add(panoFiles[i].getName());
            }
            return panoNames;
        } else {
            return  null;
        }
    }

    public static List<String> getAudioNames() {
        if (isSdCardMounted()) {
            File audioFolder = new File(Config.audioPath);
            File[] audioFiles = audioFolder.listFiles();
            List<String> panoNames = new ArrayList<>();
            for (int i = 0; i < audioFiles.length; i++) {
                panoNames.add(audioFiles[i].getName());
            }
            return panoNames;
        } else {
            return  null;
        }
    }

}
