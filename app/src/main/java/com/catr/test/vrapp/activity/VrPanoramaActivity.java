package com.catr.test.vrapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.catr.test.vrapp.Config;
import com.catr.test.vrapp.R;
import com.catr.test.vrapp.utils.MediaUtil;
import com.catr.test.vrapp.utils.VrFileUtil;
import com.catr.test.vrapp.model.VrPanoFileInfo;
import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * A basic PanoWidget Activity to load panorama images from disk. It will load a test image by
 * default. It can also load an arbitrary image from disk using:
 * adb shell am start -a "android.intent.action.VIEW" \
 * -n "com.google.vr.sdk.samples.simplepanowidget/.SimpleVrPanoramaActivity" \
 * -d "/sdcard/FILENAME.JPG"
 * <p>
 * To load stereo images, "--ei inputType 2" can be used to pass in an integer extra which will set
 * VrPanoramaView.Options.inputType.
 */
//adb shell am start -a "android.intent.action.VIEW" -n "com.google.vr.sdk.samples.simplepanowidget/.SimpleVrPanoramaActivity" -d "/sdcard/vr_res/lab_mono.jpg"
//adb shell am start -a "android.intent.action.VIEW" -n "com.google.vr.sdk.samples.simplepanowidget/.SimpleVrPanoramaActivity" -d "/sdcard/vr_res/andes_stereo.jpg" --ei inputType 2
//adb shell am start -a "android.intent.action.VIEW" -n "com.catr.test.vrapp/.activity.VrPanoramaActivity" -d "/sdcard/vr_res/lab_mono.jpg"
//Google的Demo可以用命令行打开，新建的VrApp不能用命令行打开，添加android:exported="true"后也不行
public class VrPanoramaActivity extends Activity {
    private static final String TAG = VrPanoramaActivity.class.getSimpleName();
    /**
     * Actual panorama widget.
     **/
    private VrPanoramaView panoWidgetView;
    /**
     * Arbitrary variable to track load status. In this example, this variable should only be accessed
     * on the UI thread. In a real app, this variable would be code that performs some UI actions when
     * the panorama is fully loaded.
     */
    public boolean loadImageSuccessful;
    /**
     * Tracks the file to be loaded across the lifetime of this app.
     **/
    private Uri fileUri;
    /**
     * Configuration information for the panorama.
     **/
    private VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();
    private ImageLoaderTask backgroundImageLoaderTask;


    //add by glf
    private List<VrPanoFileInfo> vrPanoFileInfos;
    private VrPanoFileInfo vrPanoFileInfo;
    private int panoramaNum = 0;

//    private TextView panoTile;
//    private TextView panoDescription;

    private Context mContext;

    private Vibrator vibrator;

    private MediaUtil mMediaUtil;

    //默认使用VR横屏模式
    private static final int DEFAULT_DISPLAYMODE = VrWidgetView.DisplayMode.EMBEDDED;
    private int vrDisplayMode;

    //第一个加载的全景图片是否完成。
    private boolean isFirstLoadSuccess = false;

    private VrPanoFileInfo blackPanoFileInfo;

    //显示黑屏标志位
    private boolean isShowBlackPano = false;

    //
    Handler mHandler;

    /**
     * Called when the app is launched via the app icon or an intent using the adb command above. This
     * initializes the app and loads the image to render.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "VrPanoramaActivity onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.creative_layout);

        panoWidgetView = (VrPanoramaView) findViewById(R.id.pano_view);
        panoWidgetView.setEventListener(new ActivityEventListener());

        Intent intent = getIntent();
        vrDisplayMode=intent.getIntExtra(VrApp.DISPLAY_MODE,DEFAULT_DISPLAYMODE);
        //默认使用VR横屏模式
        panoWidgetView.setDisplayMode(vrDisplayMode);

//        panoTile = (TextView) findViewById(R.id.tv_title);
//        panoDescription = (TextView) findViewById(R.id.tv_description);

        mContext = this;

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        blackPanoFileInfo = VrFileUtil.getBlackPanoFileInfo();

        mHandler = new Handler();

        // Initial launch of the app or an Activity recreation due to rotation.
        handleIntent(getIntent());
    }

    /**
     * Called when the Activity is already running and it's given a new intent.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "VrPanoramaActivity onNewIntent()");
        Log.i(TAG, this.hashCode() + ".onNewIntent()");
        // Save the intent. This allows the getIntent() call in onCreate() to use this new Intent during
        // future invocations.
        setIntent(intent);
        // Load the new image.
        handleIntent(intent);
    }

    /**
     * Load custom images based on the Intent or load the default image. See the Javadoc for this
     * class for information on generating a custom intent via adb.
     */
    private void handleIntent(Intent intent) {
        panoramaNum = intent.getIntExtra(VrApp.PANORAMA_NUM, 0);
        vrPanoFileInfos = VrFileUtil.getVrPanoFileInfos();
        if (vrPanoFileInfos != null && vrPanoFileInfos.size() != 0 && vrPanoFileInfos.get(panoramaNum) != null) {
            vrPanoFileInfo = vrPanoFileInfos.get(panoramaNum);
            Log.i(TAG, "show pano image " + vrPanoFileInfo.getFileName());

//            panoTile.setText(vrPanoFileInfo.getFileName());
//            panoDescription.setText(vrPanoFileInfo.getFileName());


            mMediaUtil = new MediaUtil();
            mMediaUtil.load(mContext, vrPanoFileInfo.getSoundName(), vrPanoFileInfo.getSoundUri());

            // Load the bitmap in a background thread to avoid blocking the UI thread. This operation can
            // take 100s of milliseconds.
            if (backgroundImageLoaderTask != null) {
                // Cancel any task from a previous intent sent to this activity.
                backgroundImageLoaderTask.cancel(true);
            }
            backgroundImageLoaderTask = new ImageLoaderTask();
            backgroundImageLoaderTask.execute(vrPanoFileInfo);
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "VrPanoramaActivity onPause()");
        panoWidgetView.pauseRendering();

        mMediaUtil.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "VrPanoramaActivity onResume()");
        Log.i(TAG, "VrPanoramaActivity onResume()   vrDisplayMode=" + vrDisplayMode);
        super.onResume();
        panoWidgetView.resumeRendering();

        if (isFirstLoadSuccess) {
            //gvr 0.8.5，为FULLSCREEN_VR和FULLSCREEN_MONO
            //gvr 0.9.1，改为FULLSCREEN_STEREO和FULLSCREEN_MONO
            if (vrDisplayMode == VrWidgetView.DisplayMode.FULLSCREEN_STEREO || vrDisplayMode == VrWidgetView.DisplayMode.FULLSCREEN_MONO) {
                Log.i(TAG, "VrPanoramaActivity onResume()    " + "vrDisplayMode=" + vrDisplayMode + "    mMediaUtil.play()");
                boolean playSucceed = mMediaUtil.play();
                if (!playSucceed) {
                    Log.i(TAG, "VrPanoramaActivity onResume()" + "音乐播放失败");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "VrPanoramaActivity onDestroy()");
        // Destroy the widget and free memory.
        panoWidgetView.shutdown();

        mMediaUtil.release();

        // The background task has a 5 second timeout so it can potentially stay alive for 5 seconds
        // after the activity is destroyed unless it is explicitly cancelled.
        if (backgroundImageLoaderTask != null) {
            backgroundImageLoaderTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Helper class to manage threading.
     */
    class ImageLoaderTask extends AsyncTask<VrPanoFileInfo, Void, Boolean> {

        /**
         * Reads the bitmap from disk in the background and waits until it's loaded by pano widget.
         */
        @Override
        protected Boolean doInBackground(VrPanoFileInfo... vrPanoFileInfos) {
            InputStream istr = null;
            if (null != vrPanoFileInfos && vrPanoFileInfos.length >= 1) {
                //加载黑屏全景图片时，报android.view.WindowLeaked错，逻辑稍作修改
                if (Config.versionFlag == Config.SDCARD_VERSION) {
                    if (vrPanoFileInfos[0].getFileUri() != null) {
                        try {
                            istr = new FileInputStream(new File(vrPanoFileInfos[0].getFileUri()));
                        } catch (IOException e) {
                            Log.e(TAG, "Could not load file: " + e);
                            return false;
                        }
                    } else {
                        AssetManager assetManager = getAssets();
                        try {
                            istr = assetManager.open(vrPanoFileInfos[0].getFileName());
                        } catch (IOException e) {
                            Log.e(TAG, "Could not decode default bitmap: " + e);
                            return false;
                        }
                    }
                } else if (Config.versionFlag == Config.ASSETS_VERSION && vrPanoFileInfos[0].getFileUri() == null) {
                    AssetManager assetManager = getAssets();
                    try {
                        istr = assetManager.open(vrPanoFileInfos[0].getFileName());
                    } catch (IOException e) {
                        Log.e(TAG, "Could not decode default bitmap: " + e);
                        return false;
                    }
                }
            }
            panoWidgetView.loadImageFromBitmap(BitmapFactory.decodeStream(istr), vrPanoFileInfos[0].getPanoOptions());

            try {
                istr.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close input stream: " + e);
            }
            return true;
        }
    }

    /**
     * Listen to the important events from widget.
     */
    private class ActivityEventListener extends VrPanoramaEventListener {
        /**
         * Called by pano widget on the UI thread when it's done loading the image.
         */
        @Override
        public void onLoadSuccess() {
            Log.i(TAG, "ActivityEventListener onLoadSuccess()");
            loadImageSuccessful = true;
            //第一个加载的全景图片完成！
            isFirstLoadSuccess = true;
            if (vrDisplayMode == VrWidgetView.DisplayMode.FULLSCREEN_MONO || vrDisplayMode == VrWidgetView.DisplayMode.FULLSCREEN_STEREO) {
                Log.i(TAG, "ActivityEventListener onLoadSuccess()    " + "vrDisplayMode=" + vrDisplayMode + "    mMediaUtil.play()");
                boolean playSucceed = mMediaUtil.play();
                if (!playSucceed) {
                    Log.i(TAG, "ActivityEventListener onLoadSuccess()" + "音乐播放失败");
                }
            } else {
                Log.i(TAG, "ActivityEventListener onLoadSuccess()    " + "vrDisplayMode=" + vrDisplayMode + "    mMediaUtil.stop()");
                mMediaUtil.stop();
            }

//            if (isShowBlackPano) {
//                //延时一定时间后显示下一张全景照片
//                mHandler.postDelayed(new Runnable() {
//                    public void run() {
//                        //显示下一张全景照片
//                        loadNextPano();
//                    }
//                }, 2000);
//            }

        }

        /**
         * Called by pano widget on the UI thread on any asynchronous error.
         */
        @Override
        public void onLoadError(String errorMessage) {
            Log.i(TAG, "ActivityEventListener onLoadError()");
            loadImageSuccessful = false;
            Toast.makeText(VrPanoramaActivity.this, "Error loading pano: " + errorMessage, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error loading pano: " + errorMessage);
        }

        @Override
        public void onDisplayModeChanged(int newDisplayMode) {
            Log.i(TAG, "ActivityEventListener onDisplayModeChanged()");
            super.onDisplayModeChanged(newDisplayMode);
            //记录View的DisplayMode
            vrDisplayMode = newDisplayMode;
            Log.i(TAG, "ActivityEventListener onDisplayModeChanged()     vrDisplayMode=" + vrDisplayMode);
        }

        @Override
        public void onClick() {
            Log.i(TAG, "ActivityEventListener onClick()");
            super.onClick();

            //震动
            vibrator.vibrate(50);

            //显示黑屏（带Logo）
           // loadBlackPano();
            loadNextPano();
        }
    }

    //加载黑屏全景照片
    private void loadBlackPano() {
        //设置显示黑屏标志位
        isShowBlackPano = true;
        mMediaUtil.load(mContext, blackPanoFileInfo.getSoundName(), blackPanoFileInfo.getSoundUri());
        if (backgroundImageLoaderTask != null) {
            // Cancel any task from a previous intent sent to this activity.
            backgroundImageLoaderTask.cancel(true);
        }
        backgroundImageLoaderTask = new ImageLoaderTask();
        backgroundImageLoaderTask.execute(blackPanoFileInfo);

    }

    //加载下一张全景照片及播放对应音频
    private void loadNextPano() {
        //设置显示黑屏标志位
        isShowBlackPano = false;

        panoramaNum++;
        if (panoramaNum >= vrPanoFileInfos.size())
            panoramaNum = panoramaNum - vrPanoFileInfos.size();
        vrPanoFileInfo = vrPanoFileInfos.get(panoramaNum);

//        panoTile.setText(vrPanoFileInfo.getFileName());
//        panoDescription.setText(vrPanoFileInfo.getFileName());

        mMediaUtil.load(mContext, vrPanoFileInfo.getSoundName(), vrPanoFileInfo.getSoundUri());

        // Load the bitmap in a background thread to avoid blocking the UI thread. This operation can
        // take 100s of milliseconds.
        if (backgroundImageLoaderTask != null) {
            // Cancel any task from a previous intent sent to this activity.
            backgroundImageLoaderTask.cancel(true);
        }
        backgroundImageLoaderTask = new ImageLoaderTask();
        backgroundImageLoaderTask.execute(vrPanoFileInfo);
    }
}
