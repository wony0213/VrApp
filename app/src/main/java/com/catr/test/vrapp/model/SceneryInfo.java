package com.catr.test.vrapp.model;

import com.google.common.logging.nano.Vr;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.video.VrVideoView;

/**
 * Created by Wony on 2016/9/6.
 */
public class SceneryInfo {
    public static final int INPUT_TYPE_MONO = 1;
    public static final int INPUT_TYPE_STEREO = 2;

    public static final int FORMAT_DEFAULT = 3;
    public static final int FORMAT_HLS = 4;


    private String sceneryName;
    private String city;
    private String country;

    private String pictureName;
    private String pictureUri;
    private int picResId;
    private String panoName;
    private String panoUri;
    private String videoName;
    private String videoUri;

    private int panoInputType;
    private VrPanoramaView.Options panoOptions = null;

    private int videoinputType;
    private int videoFormat;
    private VrVideoView.Options videoOptions = null;

    public SceneryInfo() {
        this.panoOptions = new VrPanoramaView.Options();
        panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
        this.videoOptions = new VrVideoView.Options();
        videoOptions.inputType = VrVideoView.Options.TYPE_MONO;
        videoOptions.inputFormat = VrVideoView.Options.FORMAT_DEFAULT;
    }

    public SceneryInfo(String sceneryName, String city, String country, String pictureName, String pictureUri, String panoName, String panoUri, String videoName, String videoUri, int panoInputType, int videoinputType, int videoFormat) {
        this.sceneryName = sceneryName;
        this.city = city;
        this.country = country;
        this.pictureName = pictureName;
        this.pictureUri = pictureUri;
        this.panoName = panoName;
        this.panoUri = panoUri;
        this.videoName = videoName;
        this.videoUri = videoUri;
        this.panoInputType = panoInputType;
        this.videoinputType = videoinputType;
        this.videoFormat = videoFormat;

        this.panoOptions = new VrPanoramaView.Options();
        switch (panoInputType) {
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
        this.videoOptions = new VrVideoView.Options();
        switch (videoinputType) {
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
        switch (videoFormat) {
            case FORMAT_DEFAULT:
                videoOptions.inputFormat = VrVideoView.Options.FORMAT_DEFAULT;
                break;
            case FORMAT_HLS:
                videoOptions.inputFormat = VrVideoView.Options.FORMAT_HLS;
                break;
            default:
                videoOptions.inputFormat = VrVideoView.Options.FORMAT_DEFAULT;
                break;
        }
    }

    public String getSceneryName() {
        return sceneryName;
    }

    public void setSceneryName(String sceneryName) {
        this.sceneryName = sceneryName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }

    public String getPanoName() {
        return panoName;
    }

    public void setPanoName(String panoName) {
        this.panoName = panoName;
    }

    public String getPanoUri() {
        return panoUri;
    }

    public void setPanoUri(String panoUri) {
        this.panoUri = panoUri;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public int getPanoInputType() {
        return panoInputType;
    }

    public void setPanoInputType(int panoInputType) {
        this.panoInputType = panoInputType;
    }

    public VrPanoramaView.Options getPanoOptions() {
        return panoOptions;
    }

    public void setPanoOptions(VrPanoramaView.Options panoOptions) {
        this.panoOptions = panoOptions;
    }

    public int getVideoinputType() {
        return videoinputType;
    }

    public void setVideoinputType(int videoinputType) {
        this.videoinputType = videoinputType;
    }

    public int getVideoFormat() {
        return videoFormat;
    }

    public void setVideoFormat(int videoFormat) {
        this.videoFormat = videoFormat;
    }

    public VrVideoView.Options getVideoOptions() {
        return videoOptions;
    }

    public void setVideoOptions(VrVideoView.Options videoOptions) {
        this.videoOptions = videoOptions;
    }

    //
    public int getPicResId() {
        return picResId;
    }

    public void setPicResId(int picResId) {
        this.picResId = picResId;
    }
}
