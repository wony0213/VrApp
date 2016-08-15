package com.catr.test.vrapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.catr.test.vrapp.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Wony on 2016/8/13.
 */
public class PermissionCheckActivity extends Activity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "PermissionCheckActivity";
    private static final int RC_READ_EXTERNAL_STORAGE_PERM = 123;

    private final Context mContext = this;

    @AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE_PERM)
    public void checkExternalStoragePerm() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Have permission, do the thing!
            Toast.makeText(mContext, "TODO: READ_EXTERNAL_STORAGE things", Toast.LENGTH_LONG).show();
            startVrPanoActivity();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera), RC_READ_EXTERNAL_STORAGE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
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

        Toast.makeText(this, "读取SD卡权限未获取，VrApp退出", Toast.LENGTH_LONG).show();
        //结束Activity
        finish();
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

    private void startVrPanoActivity() {
        //启动Activity，并finish（）
        Intent intent = new Intent(mContext, VrPanoramaActivity.class);
        //从第一张全景照片开始播放
        intent.putExtra(VrApp.PANORAMA_NUM, 0);
        startActivity(intent);
        //结束LaunchActivity
        finish();
    }

}
