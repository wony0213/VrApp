package com.catr.test.vrapp.utils;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;

/**
 * Created by Wony on 2016/7/28.
 */
public class VrPanoFileInfo{
    public static final int INPUT_TYPE_MONO = 1;
    public static final int INPUT_TYPE_STEREO = 2;

    private String fileName;
    private String fileTitle;
    private String fileUri;
    private String soundName;
    private String soundUri;
    private int inputType;
    private VrPanoramaView.Options panoOptions = null;
    private int order;

    public VrPanoFileInfo() {
        this.panoOptions = new VrPanoramaView.Options();
        panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
    }

    public VrPanoFileInfo(String fileName, String fileTitle, String fileUri, String soundName, String soundUri, int inputType) {
        this.fileName = fileName;
        this.fileTitle = fileTitle;
        this.fileUri = fileUri;
        this.soundName = soundName;
        this.soundUri = soundUri;
        this.inputType = inputType;

        this.panoOptions = new VrPanoramaView.Options();
        switch (inputType) {
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
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public String getFileUri() {
        return fileUri;
    }

    public String getSoundName() {
        return soundName;
    }

    public String getSoundUri() {
        return soundUri;
    }

    public int getInputType() {
        return inputType;
    }

    public VrPanoramaView.Options getPanoOptions() {
        return panoOptions;
    }

    public int getOrder() {
        return order;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public void setSoundUri(String soundUri) {
        this.soundUri = soundUri;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
        switch (inputType) {
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
    }

    public void setPanoOptions(VrPanoramaView.Options panoOptions) {
        this.panoOptions = panoOptions;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
