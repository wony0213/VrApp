package com.catr.test.vrapp.utils;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.video.VrVideoView;

/**
 * Created by Wony on 2016/7/28.
 */
public class VrFileInfo {
    public static final int FILE_TYPE_PANORAMA = 1;
    public static final int FILE_TYPE_VIDEO = 2;

    public static final int INPUT_TYPE_MONO = 3;
    public static final int INPUT_TYPE_STEREO = 4;

    private String fileName;
    private int fileType;
    private int fileInputType;
    private String fileUri;
    private VrPanoramaView.Options panoOptions = null;
    private VrVideoView.Options videoOptions = null;

    public VrFileInfo(String fileName, int fileType, int fileInputType, String fileUri) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileInputType = fileInputType;
        this.fileUri = fileUri;
        if (fileType == FILE_TYPE_PANORAMA) {
            panoOptions = new VrPanoramaView.Options();
            switch (fileInputType) {
                case INPUT_TYPE_MONO:
                    panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
                    break;
                case INPUT_TYPE_STEREO:
                    panoOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
                    break;
                default:
                    panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
                    break;
            }
        } else if (fileType == FILE_TYPE_VIDEO) {
            videoOptions = new VrVideoView.Options();
            switch (fileInputType) {
                case INPUT_TYPE_MONO:
                    videoOptions.inputType = VrVideoView.Options.TYPE_MONO;
                    break;
                case INPUT_TYPE_STEREO:
                    videoOptions.inputType = VrVideoView.Options.TYPE_STEREO_OVER_UNDER;
                    break;
                default:
                    videoOptions.inputType = VrVideoView.Options.TYPE_MONO;
                    break;
            }
        }
    }

}
