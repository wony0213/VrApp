package com.catr.test.vrapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.catr.test.vrapp.Config;
import com.catr.test.vrapp.R;
import com.catr.test.vrapp.qrcode.QrCodeUtils;
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


public class VrPanoramaActivity extends Activity {
    private static final String TAG = VrPanoramaActivity.class.getSimpleName();

    //全景照片显示View
    private VrPanoramaView panoWidgetView;
    //全景照片加载成功标志位
    public boolean loadImageSuccessful;
    //异步加载全景照片Task
    private ImageLoaderTask backgroundImageLoaderTask;


    //全景照片文件信息列表
    private List<VrPanoFileInfo> vrPanoFileInfos;
    //当前显示的全景照片文件信息
    private VrPanoFileInfo vrPanoFileInfo;
    //记录当前显示的全景照片在文件信息列表中的位置
    private int panoramaNum = 0;


    //Activity上下文
    private Context mContext;
    //
    private Handler mHandler;
    //
    private Vibrator mVibrator;

    //默认使用VR横屏模式
    private static final int DEFAULT_DISPLAYMODE = VrWidgetView.DisplayMode.EMBEDDED;
    //VrWidgetView显示模式
    private int vrDisplayMode;


    //默认使用手动播放模式
    private static final String AUTO_PLAY_MODE = "auto_play_mode";
    private static final String MANUAL_PLAY_MODE = "manual_play_mode";
    private static final String DEFAULT_PLAY_MODE = AUTO_PLAY_MODE;
    //全景照片播放模式
    private String playMode;


    //自动播放时间间隔3秒钟（语音播放完之后）
    private static final int AUTO_PLAY_INTERVAL = 3;
    //MediarPlayer播放完成监听
    private MediaPlayer.OnCompletionListener mOnCompletionListener = null;


    //黑屏全景照片文件信息
    private VrPanoFileInfo blackPanoFileInfo;
    //显示黑屏标志位
    private boolean isShowBlackPano = false;


    //第一个加载的全景图片是否完成。
    private boolean isFirstLoadSuccess = false;
    //照片切换中标志位，防止响应多次点击
    private boolean isPanoSwitching = false;


    //播放全景照片按钮
    private Button playButton;
    //二维码图片
    private ImageView qrCodeWeiboImageview;
    private ImageView qrCodeWeixinImageview;
    //文本域
    private TextView mDetailTextView;
    //翻转icon
    private ImageView mTurnOverImageView;
    //TextView设置默认最大展示行数为3
    private static final int MAX_TEXTVIEW_LINE_NUM = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "VrPanoramaActivity onCreate()");

        setContentView(R.layout.detail_layout);

        //初始化
        mContext = this;
        mHandler = new Handler();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        //设置panoWidgetView
        panoWidgetView = (VrPanoramaView) findViewById(R.id.pano_view);
        panoWidgetView.setEventListener(new ActivityEventListener());
        //默认使用VR横屏模式
        Intent intent = getIntent();
        vrDisplayMode = intent.getIntExtra(VrApp.DISPLAY_MODE, DEFAULT_DISPLAYMODE);
        panoWidgetView.setDisplayMode(vrDisplayMode);
        //去除info按钮
        panoWidgetView.setInfoButtonEnabled(false);


        //设置播放模式,默认使用手动播放模式
        playMode = intent.getStringExtra(VrApp.PLAY_MODE);
        if (null == playMode) {
            playMode = DEFAULT_PLAY_MODE;
        }


        //初始化文字显示，及展开效果
        mDetailTextView = (TextView) findViewById(R.id.detail_text);
        mTurnOverImageView = (ImageView) findViewById(R.id.turn_over_icon);
        //设置默认显示高度
        mDetailTextView.setHeight(mDetailTextView.getLineHeight() * MAX_TEXTVIEW_LINE_NUM);
        //根据高度来控制是否展示翻转icon
        mDetailTextView.post(new Runnable() {
            @Override
            public void run() {
                mTurnOverImageView.setVisibility(mDetailTextView.getLineCount() > MAX_TEXTVIEW_LINE_NUM ? View.VISIBLE : View.GONE);
            }
        });
        //翻转监听
        mTurnOverImageView.setOnClickListener(new MyTurnListener());


        //初始化播放按钮功能
        playButton = (Button) findViewById(R.id.btn_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFirstPanoAndPlay();
            }
        });


        //初始化长按二维码跳转官方微博功能
        qrCodeWeiboImageview = (ImageView) findViewById(R.id.img_qr_weibo);
        qrCodeWeiboImageview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) qrCodeWeiboImageview.getBackground();
                QrCodeUtils.analyzeBitmap(bitmapDrawable.getBitmap(), new QrCodeUtils.AnalyzeCallback() {
                    @Override
                    public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                        //Toast.makeText(mContext, "解析结果:" + result, Toast.LENGTH_LONG).show();
                        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                        mContext.startActivity(mIntent);
                    }

                    @Override
                    public void onAnalyzeFailed() {
                        //Toast.makeText(mContext, "解析二维码失败", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            }
        });


        //初始化全景照片文件信息列表
        vrPanoFileInfos = VrFileUtil.getVrPanoFileInfos();
        //初始化黑屏全景照片文件信息
        blackPanoFileInfo = VrFileUtil.getBlackPanoFileInfo();


        //自动播放模式下，初始化mOnCompletionListener
        if (playMode == AUTO_PLAY_MODE) {
            mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadNextPano();
                        }
                    }, AUTO_PLAY_INTERVAL * 1000);

                }
            };
        }

        //处理Intent信息，加载相应全景照片信息
        handleIntent(getIntent());
    }


    //Activity已经运行的情况下收到新的Intent,调用该方法
    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "VrPanoramaActivity onNewIntent()");
        // Save the intent. This allows the getIntent() call in onCreate() to use this new Intent during future invocations.
        setIntent(intent);
        // Load the new image.
        handleIntent(intent);
    }


    //处理Intent，根据panoramaNum，从vrPanoFileInfos中加载相应的全景照片
    private void handleIntent(Intent intent) {
        panoramaNum = intent.getIntExtra(VrApp.PANORAMA_NUM, 0);

        if (vrPanoFileInfos != null && vrPanoFileInfos.size() != 0 && vrPanoFileInfos.get(panoramaNum) != null) {
            vrPanoFileInfo = vrPanoFileInfos.get(panoramaNum);
            Log.i(TAG, "show pano image " + vrPanoFileInfo.getFileName());

            MediaUtil.load(mContext, vrPanoFileInfo.getSoundResId(), vrPanoFileInfo.getSoundUri());

            if (backgroundImageLoaderTask != null) {
                //取消已经加载中的任务
                backgroundImageLoaderTask.cancel(true);
            }
            backgroundImageLoaderTask = new ImageLoaderTask();
            backgroundImageLoaderTask.execute(vrPanoFileInfo);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(TAG, "VrPanoramaActivity onPause()");

        panoWidgetView.pauseRendering();

        MediaUtil.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "VrPanoramaActivity onResume()");
        Log.i(TAG, "VrPanoramaActivity onResume()   vrDisplayMode=" + vrDisplayMode);

        panoWidgetView.resumeRendering();

        if (isFirstLoadSuccess) {
            if (vrDisplayMode == VrWidgetView.DisplayMode.FULLSCREEN_STEREO || vrDisplayMode == VrWidgetView.DisplayMode.FULLSCREEN_MONO) {
                Log.i(TAG, "VrPanoramaActivity onResume()    " + "vrDisplayMode=" + vrDisplayMode + "    mMediaUtil.play()");
                boolean playSucceed = MediaUtil.play();
                //设置自动播放
                if (playMode == AUTO_PLAY_MODE) {
                    MediaUtil.setOnCompletionListener(mOnCompletionListener);
                }
                if (!playSucceed) {
                    Log.i(TAG, "VrPanoramaActivity onResume()" + "音乐播放失败");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "VrPanoramaActivity onDestroy()");

        // Destroy the widget and free memory.
        panoWidgetView.shutdown();

        MediaUtil.release();

        // The background task has a 5 second timeout so it can potentially stay alive for 5 seconds after the activity is destroyed unless it is explicitly cancelled.
        if (backgroundImageLoaderTask != null) {
            backgroundImageLoaderTask.cancel(true);
        }
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
                boolean playSucceed = MediaUtil.play();
                //设置自动播放
                if (playMode == AUTO_PLAY_MODE) {
                    MediaUtil.setOnCompletionListener(mOnCompletionListener);
                }
                if (!playSucceed) {
                    Log.i(TAG, "ActivityEventListener onLoadSuccess()" + "音乐播放失败");
                }
            } else {
                Log.i(TAG, "ActivityEventListener onLoadSuccess()    " + "vrDisplayMode=" + vrDisplayMode + "    mMediaUtil.stop()");
                MediaUtil.stop();
            }

            //全景照片切换结束
            isPanoSwitching = false;

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

            //仅在全屏模式下相应点击事件
            if (vrDisplayMode == VrWidgetView.DisplayMode.FULLSCREEN_STEREO || vrDisplayMode == VrWidgetView.DisplayMode.FULLSCREEN_MONO) {
                if (!isPanoSwitching) {
                    //全景照片开始切换
                    isPanoSwitching = true;

                    //震动
                    mVibrator.vibrate(50);

                    //显示黑屏（带Logo）
                    // loadBlackPano();
                    loadNextPano();
                }
            }
        }
    }

    //加载黑屏全景照片
    private void loadBlackPano() {
        //设置显示黑屏标志位
        isShowBlackPano = true;
        MediaUtil.load(mContext, blackPanoFileInfo.getSoundResId(), blackPanoFileInfo.getSoundUri());
        if (backgroundImageLoaderTask != null) {
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

        //如果切换回第一张，则结束播放
        if (panoramaNum == 0) {
            panoWidgetView.setDisplayMode(VrWidgetView.DisplayMode.EMBEDDED);
        }

        MediaUtil.load(mContext, vrPanoFileInfo.getSoundResId(), vrPanoFileInfo.getSoundUri());

        if (backgroundImageLoaderTask != null) {
            backgroundImageLoaderTask.cancel(true);
        }
        backgroundImageLoaderTask = new ImageLoaderTask();
        backgroundImageLoaderTask.execute(vrPanoFileInfo);
    }

    //加载第一张全景照片及播放对应音频
    private void loadFirstPanoAndPlay() {
        //设置显示黑屏标志位
        isShowBlackPano = false;

        if (panoramaNum == 0) {
            //如果panoramaNum为0，则只需要切换DisplayMode即可
            panoWidgetView.setDisplayMode(VrWidgetView.DisplayMode.FULLSCREEN_STEREO);
        } else {
            //如果panoramaNum不为0，则需要load第一张全景照片
            panoramaNum = 0;
            vrPanoFileInfo = vrPanoFileInfos.get(panoramaNum);

            MediaUtil.load(mContext, vrPanoFileInfo.getSoundResId(), vrPanoFileInfo.getSoundUri());

            //使用双眼模式
            panoWidgetView.setDisplayMode(VrWidgetView.DisplayMode.FULLSCREEN_STEREO);

            if (backgroundImageLoaderTask != null) {
                backgroundImageLoaderTask.cancel(true);
            }
            backgroundImageLoaderTask = new ImageLoaderTask();
            backgroundImageLoaderTask.execute(vrPanoFileInfo);
        }
    }


    //折叠、展开效果
    private class MyTurnListener implements View.OnClickListener {

        boolean isExpand;  //是否翻转

        @Override
        public void onClick(View v) {
            isExpand = !isExpand;
            mDetailTextView.clearAnimation();  //清除动画
            final int tempHight;
            final int startHight = mDetailTextView.getHeight();  //起始高度
            int durationMillis = 200;

            if (isExpand) {
                /**
                 * 折叠效果，从长文折叠成短文
                 */
                tempHight = mDetailTextView.getLineHeight() * mDetailTextView.getLineCount() - startHight;  //为正值，长文减去短文的高度差
                //翻转icon的180度旋转动画
                RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(durationMillis);
                animation.setFillAfter(true);
                mTurnOverImageView.startAnimation(animation);
            } else {
                /**
                 * 展开效果，从短文展开成长文
                 */
                tempHight = mDetailTextView.getLineHeight() * MAX_TEXTVIEW_LINE_NUM - startHight;//为负值，即短文减去长文的高度差
                //翻转icon的180度旋转动画
                RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(durationMillis);
                animation.setFillAfter(true);
                mTurnOverImageView.startAnimation(animation);
            }

            Animation animation = new Animation() {
                //interpolatedTime 为当前动画帧对应的相对时间，值总在0-1之间
                protected void applyTransformation(float interpolatedTime, Transformation t) { //根据ImageView旋转动画的百分比来显示textview高度，达到动画效果
                    mDetailTextView.setHeight((int) (startHight + tempHight * interpolatedTime));//原始长度+高度差*（从0到1的渐变）即表现为动画效果
                }
            };
            animation.setDuration(durationMillis);
            mDetailTextView.startAnimation(animation);
        }
    }
}
