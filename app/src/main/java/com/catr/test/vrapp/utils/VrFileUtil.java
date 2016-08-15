package com.catr.test.vrapp.utils;

import android.util.Log;

import com.catr.test.vrapp.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Wony on 2016/7/28.
 */
public class VrFileUtil {
    private static final String TAG = "VrFileUtil";

    public static List<VrPanoFileInfo> vrPanoFileInfos;

    public static List<VrPanoFileInfo> getVrPanoFileInfos() {
        if (Config.versionFlag == Config.SDCARD_VERSION) {
            //从SD卡读取pano文件信息
            vrPanoFileInfos = new ArrayList<>();

            List<String> panoNames = SdcardUtil.getPanoNames();
            List<String> audioNames = SdcardUtil.getAudioNames();

            for (int i = 0; i < panoNames.size(); i++) {
                String[] panoInfos = panoNames.get(i).split("-");
                if (panoInfos.length == 3) {
                    //pano文件信息，顺序信息
                    String fileNameString = panoInfos[0] + ".jpg";
                    String inputTypeString = panoInfos[1];
                    String orderString = panoInfos[2].substring(0, panoInfos[2].indexOf("."));
                    String audioNameString = panoInfos[0] + ".mp3";

                    VrPanoFileInfo vrPanoFileInfo = new VrPanoFileInfo();
                    vrPanoFileInfo.setFileName(fileNameString);
                    vrPanoFileInfo.setFileTitle(fileNameString);
                    vrPanoFileInfo.setFileUri(Config.panoPath + panoNames.get(i));
                    vrPanoFileInfo.setOrder(Integer.parseInt(orderString));
                    if (inputTypeString.equals("mono")) {
                        vrPanoFileInfo.setInputType(VrPanoFileInfo.INPUT_TYPE_MONO);
                    } else if (inputTypeString.equals("stereo")){
                        vrPanoFileInfo.setInputType(VrPanoFileInfo.INPUT_TYPE_STEREO);
                    }

                    //对应的audio信息
                    if (audioNames.contains(audioNameString)) {
                        vrPanoFileInfo.setSoundName(audioNameString);
                        vrPanoFileInfo.setSoundUri(Config.audioPath + audioNameString);
                    }

                    vrPanoFileInfos.add(vrPanoFileInfo);
                }
            }

            Collections.sort(vrPanoFileInfos, new Comparator<VrPanoFileInfo>() {
                @Override
                public int compare(VrPanoFileInfo lhs, VrPanoFileInfo rhs) {
                    return (lhs.getOrder() - rhs.getOrder());
                }
            });
            return vrPanoFileInfos;
        } else {
            //从asset文件夹读取文件信息，手动添加
            vrPanoFileInfos = new ArrayList<>();

            vrPanoFileInfos.add(new VrPanoFileInfo("caict-mono-1.jpg", "中国信息通信研究院", null, "caict.mp3", null, VrPanoFileInfo.INPUT_TYPE_MONO));
            vrPanoFileInfos.add(new VrPanoFileInfo("lab_ten_floor-mono-2.jpg", "十楼实验室", null, "lab_ten_floor.mp3", null, VrPanoFileInfo.INPUT_TYPE_MONO));
            vrPanoFileInfos.add(new VrPanoFileInfo("lab_three_floor-mono-3.jpg", "三楼实验室", null, "lab_three_floor.mp3", null, VrPanoFileInfo.INPUT_TYPE_MONO));

            return vrPanoFileInfos;
        }
    }

    public static VrPanoFileInfo getBlackPanoFileInfo() {
        VrPanoFileInfo vrPanoFileInfo = new VrPanoFileInfo("black-mono.jpg", "全景黑屏带Logo", null, null, null, VrPanoFileInfo.INPUT_TYPE_MONO);
        return  vrPanoFileInfo;
    }
}
