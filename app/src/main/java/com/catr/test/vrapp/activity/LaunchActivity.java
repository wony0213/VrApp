package com.catr.test.vrapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.catr.test.vrapp.Config;
import com.catr.test.vrapp.R;
import com.catr.test.vrapp.appintro.AppIntroActivity;

/**
 * Created by Wony on 2016/8/13.
 */
public class LaunchActivity extends PermissionCheckActivity {

    private static final String TAG = "LaunchActivity";

    private final Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //启动AppIntro
//        startAppIntro();

        //
        final View view = View.inflate(this, R.layout.activity_launch, null);
        setContentView(view);

        //渐变展示启动屏
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        alphaAnimation.setDuration(3000);
        view.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (Config.versionFlag == Config.SDCARD_VERSION) {
                    checkExternalStoragePerm(new PermissionGrantedCallback() {
                        @Override
                        public void doSomething() {
                            if (VrApp.isFirstStart(mContext)) {
                                startAppIntro();
                                VrApp.setFirstStart(mContext);
                            } else {
                                startVrPanoActivity();
                            }
                        }
                    });
                } else if (Config.versionFlag == Config.ASSETS_VERSION){
                    if (VrApp.isFirstStart(mContext)) {
                        startAppIntro();
                        VrApp.setFirstStart(mContext);
                    } else {
                        startVrPanoActivity();
                    }
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startAppIntro() {
        // 启动Activity，并finish（）
        Intent i = new Intent(this, AppIntroActivity.class);
        startActivity(i);
        //结束LaunchActivity
        finish();
    }

    private void startMainActivity() {
        //启动Activity，并finish（）
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //结束LaunchActivity
        finish();
    }

    private void startVrPanoActivity() {
        //启动Activity，并finish（）
        Intent intent = new Intent(this, VrPanoramaActivity.class);
        //从第一张全景照片开始播放
        intent.putExtra(VrApp.PANORAMA_NUM, 0);
        startActivity(intent);
        //结束LaunchActivity
        finish();
    }
}
