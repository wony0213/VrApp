package com.catr.test.vrapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.catr.test.vrapp.R;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Wony on 2016/8/13.
 */
public class PermissionCheckActivity extends Activity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "PermissionCheckActivity";

    private static final int RC_READ_EXTERNAL_STORAGE_PERM = 123;

    private final Context mContext = this;

    private boolean sdcardPermisionDeniedFirstTime = true;

    private PermissionGrantedCallback mSdcardPermissionGrantedCallback = null;

    //@AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE_PERM)
    //放弃使用AfterPermissionGranted注释，Activity继承后，该注解涉及的逻辑执行不正常，需要查easypermissions工程的源码，private static void runAnnotatedMethods(Object object, int requestCode)
    //改为使用在onPermissionsGranted方法中根据requestCode手动调用，实现相同逻辑。
    public void checkExternalStoragePerm(PermissionGrantedCallback permissionGrantedCallback) {
        Log.d(TAG, "checkExternalStoragePerm（）");
        mSdcardPermissionGrantedCallback = permissionGrantedCallback;
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //已获取SD卡权限
            Toast.makeText(this, "已获取SD卡权限，VrApp可正常运行", Toast.LENGTH_SHORT).show();
            if (null != mSdcardPermissionGrantedCallback) {
                mSdcardPermissionGrantedCallback.doSomething();
            }
        } else {
            //申请SD卡权限
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_read_external_storage), RC_READ_EXTERNAL_STORAGE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        if (requestCode == RC_READ_EXTERNAL_STORAGE_PERM) {
            checkExternalStoragePerm(mSdcardPermissionGrantedCallback);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied permissions and checked NEVER ASK AGAIN.
        // This will display a dialog directing them to enable the permission in app settings.
        //EasyPermissions.checkDeniedPermissionsNeverAskAgain(this, getString(R.string.rationale_ask_again), R.string.setting, R.string.cancel, null, perms);

        if (requestCode == RC_READ_EXTERNAL_STORAGE_PERM) {
            if (sdcardPermisionDeniedFirstTime) {
                sdcardPermisionDeniedFirstTime = false;
                // Ask for one permission
                EasyPermissions.requestPermissions(this, getString(R.string.rationale_read_external_storage), RC_READ_EXTERNAL_STORAGE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                Toast.makeText(this, "读取SD卡权限未获取，VrApp退出", Toast.LENGTH_LONG).show();
                //结束Activity
                finish();
            }
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

    public interface PermissionGrantedCallback {
        void doSomething();
    }

}
