package com.catr.test.vrapp.appintro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.catr.test.vrapp.R;
import com.catr.test.vrapp.activity.VrApp;
import com.catr.test.vrapp.activity.VrPanoramaActivity;
import com.google.vr.sdk.widgets.common.VrWidgetView;


public class AppIntroActivity extends BaseAppIntro {

    private static final String TAG = "LaunchActivity";

//    private Context mContext = this;

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(SampleSlide.newInstance(R.layout.intro));
        addSlide(SampleSlide.newInstance(R.layout.intro2));
        addSlide(SampleSlide.newInstance(R.layout.intro3));
        addSlide(SampleSlide.newInstance(R.layout.intro4));

        setFadeAnimation();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, VrPanoramaActivity.class);
        startActivity(intent);
        //结束Activity
        finish();
    }

    private void startVrPanoActivity(int display_mode) {
        //启动Activity，并finish（）
        Intent intent = new Intent(this, VrPanoramaActivity.class);
        //从第一张全景照片开始播放
        intent.putExtra(VrApp.PANORAMA_NUM, 0);
        intent.putExtra(VrApp.DISPLAY_MODE, display_mode);
        startActivity(intent);
        //结束LaunchActivity
        finish();
    }

//    @Override
//    public void onSkipPressed() {
//        startVrPanoActivity();
//    }

//    @Override
//    public void onNextPressed() {
//
//    }

//    @Override
//    public void onDonePressed() {
//        startVrPanoActivity();
//    }

    @Override
    public void onSlideChanged() {

    }

    public void getStarted(View v) {
        startVrPanoActivity(VrWidgetView.DisplayMode.FULLSCREEN_STEREO);
    }
}
