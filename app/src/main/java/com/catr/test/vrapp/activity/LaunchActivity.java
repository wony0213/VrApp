package com.catr.test.vrapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.catr.test.vrapp.R;
import com.catr.test.vrapp.appintro.AppIntroActivity;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Wony on 2016/8/13.
 */
public class LaunchActivity extends Activity implements EasyPermissions.PermissionCallbacks{

    private static final String TAG = "LaunchActivity";
    private static final int RC_READ_EXTERNAL_STORAGE_PERM = 123;

//    private final Context mContext = this;

    private boolean permisionDeniedFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //启动AppIntro
        startAppIntro();

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
                //检查权限并启动VrPanoActivity
                checkExternalStoragePerm();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startAppIntro() {
        //  Initialize SharedPreferences
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        //  Create a new boolean and preference and set it to true
        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

        //  If the activity has never started before...
        if (isFirstStart) {

            //  Launch app intro
            Intent i = new Intent(this, AppIntroActivity.class);
            startActivity(i);

            //  Make a new preferences editor
            SharedPreferences.Editor e = getPrefs.edit();

            //  Edit preference to make it false because we don't want this to run again
            e.putBoolean("firstStart", false);

            //  Apply changes
            e.apply();

            //结束LaunchActivity
            finish();
        }
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

    @AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE_PERM)
    public void checkExternalStoragePerm() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Have permission, do the thing!
            Toast.makeText(this, "TODO: READ_EXTERNAL_STORAGE things", Toast.LENGTH_LONG).show();
            startVrPanoActivity();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_read_external_storage), RC_READ_EXTERNAL_STORAGE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied permissions and checked NEVER ASK AGAIN.
        // This will display a dialog directing them to enable the permission in app settings.
        EasyPermissions.checkDeniedPermissionsNeverAskAgain(this, getString(R.string.rationale_ask_again), R.string.setting, R.string.cancel, null, perms);

        if (permisionDeniedFirstTime) {
            permisionDeniedFirstTime = false;
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_read_external_storage), RC_READ_EXTERNAL_STORAGE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            Toast.makeText(this, "读取SD卡权限未获取，VrApp退出", Toast.LENGTH_LONG).show();
            //结束Activity
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
