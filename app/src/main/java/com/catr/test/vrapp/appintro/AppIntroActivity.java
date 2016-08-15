package com.catr.test.vrapp.appintro;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.catr.test.vrapp.R;
import com.catr.test.vrapp.activity.VrApp;
import com.catr.test.vrapp.activity.VrPanoramaActivity;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class AppIntroActivity extends BaseAppIntro implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "LaunchActivity";
    private static final int RC_READ_EXTERNAL_STORAGE_PERM = 123;

//    private Context mContext = this;

    private boolean permisionDeniedFirstTime = true;

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

    private void startVrPanoActivity() {
        Intent intent = new Intent(this, VrPanoramaActivity.class);
        //从第一张全景照片开始播放
        intent.putExtra(VrApp.PANORAMA_NUM, 0);
        startActivity(intent);
        //结束Activity
        finish();
    }

    @Override
    public void onSkipPressed() {
        //检查权限并启动VrPanoActivity
        checkExternalStoragePerm();
    }


    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        //检查权限并启动VrPanoActivity
        checkExternalStoragePerm();
    }


    @Override
    public void onSlideChanged() {

    }

//    public void getStarted(View v) {
//        startVrPanoActivity();
//    }


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
