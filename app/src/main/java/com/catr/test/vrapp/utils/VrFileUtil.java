package com.catr.test.vrapp.utils;

import com.catr.test.vrapp.Config;
import com.catr.test.vrapp.R;
import com.catr.test.vrapp.model.VrPanoFileInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Wony on 2016/7/28.
 */
public class VrFileUtil {
    private static final String TAG = "VrFileUtil";

    private static String[] fileNames = {"yjyjm-mono-010.jpg", "3gdlwg-mono-020.jpg", "3gdl1c-mono-030.jpg", "8cyzxsys-mono-050.jpg", "10cmnwsys-mono-060.jpg", "dbasw-mono-070.jpg", "smfdbas-mono-080.jpg", "stxqdbas-mono-090.jpg", "mimoota-mono-100.jpg", "dttotasys-mono-110.jpg", "wifilmyzsys-mono-120.jpg", "dcfssys-mono-130.jpg", "sarcss-mono-140.jpg", "xys-mono-150.jpg", "ydrjyzsys-mono-160.jpg", "js-mono-170.jpg"};
    private static String[] fileTitles = {"01 研究院进门", "02 3G大楼外观", "03 3G大楼一层", "05 8层一致性实验室", "06 10层模拟实验网", "07 电波暗室外", "08 十米法电波暗室", "09 双天线全电波暗室", "10 MIMO OTA", "11 多探头 OTA暗室", "12 Wi-Fi联盟验证实验室", "13 电磁辐射生化实验室", "14 SAR测试", "15 消音室", "16 移动软件验证实验室", "17 结束"};
    private static String[] soundNames = {"yjyjm.m4a", "3gdlwg.m4a", "3gdl1c.m4a", "8cyzxsys.m4a", "10cmnwsys.m4a", "dbasw.m4a", "smfdbas.m4a", "stxqdbas.m4a", "mimoota.m4a", "dttotasys.m4a", "wifilmyzsys.m4a", "dcfssys.m4a", "sarcss.m4a", "xys.m4a", "ydrjyzsys.m4a", "js.m4a"};

    private static int[] soundResIds = {R.raw.yjyjm, R.raw.gdlwg, R.raw.gdl1c, R.raw.cyzxsys, R.raw.cmnwsys, R.raw.dbasw, R.raw.smfdbas, R.raw.stxqdbas, R.raw.mimoota, R.raw.dttotasys, R.raw.wifilmyzsys, R.raw.dcfssys, R.raw.sarcss, R.raw.xys, R.raw.ydrjyzsys, R.raw.js};

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
                    String audioNameStringMp3 = panoInfos[0] + ".mp3";
                    String audioNameStringM4a = panoInfos[0] + ".m4a";

                    VrPanoFileInfo vrPanoFileInfo = new VrPanoFileInfo();
                    vrPanoFileInfo.setFileName(fileNameString);
                    vrPanoFileInfo.setFileTitle(fileNameString);
                    vrPanoFileInfo.setFileUri(Config.panoPath + panoNames.get(i));
                    vrPanoFileInfo.setOrder(Integer.parseInt(orderString));
                    if (inputTypeString.equals("mono")) {
                        vrPanoFileInfo.setInputType(VrPanoFileInfo.INPUT_TYPE_MONO);
                    } else if (inputTypeString.equals("stereo")) {
                        vrPanoFileInfo.setInputType(VrPanoFileInfo.INPUT_TYPE_STEREO);
                    }

                    //对应的audio信息
                    if (audioNames.contains(audioNameStringMp3)) {
                        vrPanoFileInfo.setSoundName(audioNameStringMp3);
                        vrPanoFileInfo.setSoundUri(Config.audioPath + audioNameStringMp3);
                    } else if (audioNames.contains(audioNameStringM4a)) {
                        vrPanoFileInfo.setSoundName(audioNameStringM4a);
                        vrPanoFileInfo.setSoundUri(Config.audioPath + audioNameStringM4a);
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
            //从asset文件夹读取文件信息，手动添加,按顺序
            vrPanoFileInfos = new ArrayList<>();

            for (int i = 0; i < fileNames.length; i++) {
                vrPanoFileInfos.add(new VrPanoFileInfo(fileNames[i], fileTitles[i], null, soundNames[i], soundResIds[i], null, VrPanoFileInfo.INPUT_TYPE_MONO));
            }

//            vrPanoFileInfos.add(new VrPanoFileInfo("yjyjm-mono-010.jpg", "01 研究院进门", null, "yjyjm.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("3gdlwg-mono-020.jpg", "02 3G大楼外观", null, "3gdlwg.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("3gdl1c-mono-030.jpg", "03 3G大楼一层", null, "3gdl1c.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("8cyzxsys-mono-050.jpg", "05 8层一致性实验室", null, "8cyzxsys.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("10cmnwsys-mono-060.jpg", "06 10层模拟实验网", null, "10cmnwsys.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("dbasw-mono-070.jpg", "07 电波暗室外", null, "dbasw.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("smfdbas-mono-080.jpg", "08 十米法电波暗室", null, "smfdbas.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("stxqdbas-mono-090.jpg", "09 双天线全电波暗室", null, "stxqdbas.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("mimoota-mono-100.jpg", "10 MIMO OTA", null, "mimoota.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("dttotasys-mono-110.jpg", "11 多探头 OTA暗室", null, "dttotasys.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("wifilmyzsys-mono-120.jpg", "12 Wi-Fi联盟验证实验室", null, "wifilmyzsys.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("dcfssys-mono-130.jpg", "13 电磁辐射生化实验室", null, "dcfssys.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("sarcss-mono-140.jpg", "14 SAR测试", null, "sarcss.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("xys-mono-150.jpg", "15 消音室", null, "xys.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("ydrjyzsys-mono-160.jpg", "16 移动软件验证实验室", null, "ydrjyzsys.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));
//            vrPanoFileInfos.add(new VrPanoFileInfo("xpcs-mono-170.jpg", "17 芯片测试", null, "xpcs.m4a", null, null, VrPanoFileInfo.INPUT_TYPE_MONO));

            return vrPanoFileInfos;
        }
    }

    public static VrPanoFileInfo getBlackPanoFileInfo() {
        VrPanoFileInfo vrPanoFileInfo = new VrPanoFileInfo("black-mono.jpg", "全景黑屏带Logo", null, null, null, null, VrPanoFileInfo.INPUT_TYPE_MONO);
        return vrPanoFileInfo;
    }
}
