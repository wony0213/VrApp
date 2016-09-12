package com.catr.test.vrapp.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.catr.test.vrapp.Config;
import com.catr.test.vrapp.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wony on 2016/7/28.
 */
public class MediaUtil {
    private static final String TAG = "MediaUtil";

    private static final int STATUS_STOPED = 0;
    private static final int STATUS_PLAYING = 1;
    private static final int STATUS_PAUSED = 2;
    private static int mStatus = STATUS_STOPED;
    private static boolean isLoaded = false;

    private static MediaPlayer mediaPlayer = null;
    private static Context mContext;
    private static Integer mMediaResId;
    private static String mMediaPath;


    public static void load(Context context, Integer mediaResId, String mediaPath) {
        //重新加载音乐前，先确保一直的MediaPlayer stop并release
        release();

        //初始化mMediaResId、mediaPath
        mContext = context;
        mMediaResId = mediaResId;
        mMediaPath = mediaPath;

        //
        isLoaded = true;
    }

    private static boolean doPlay() {
        switch (mStatus) {
            case STATUS_STOPED:
                mediaPlayer.start();
                setStatus(STATUS_PLAYING);
                break;
            case STATUS_PAUSED:
                mediaPlayer.start();
                setStatus(STATUS_PLAYING);
                break;
            case STATUS_PLAYING:
                break;
            default:
                break;
        }
        return true;
    }

    public static boolean play() {
        initMediaPlayer();
        if (null != mediaPlayer && isLoaded) {
            return doPlay();
        } else {
            return false;
        }
    }

    private static void initMediaPlayer() {
        if (Config.versionFlag == Config.SDCARD_VERSION && null != mMediaPath) {
            mediaPlayer = MediaPlayer.create(mContext, Uri.parse("file://" + mMediaPath));
        } else if (Config.versionFlag == Config.ASSETS_VERSION && null != mMediaResId) {
            mediaPlayer = MediaPlayer.create(mContext, mMediaResId.intValue());
        } else {
            mediaPlayer = null;
        }
    }

    private static void doStop() {
        switch (mStatus) {
            case STATUS_STOPED:
                break;
            case STATUS_PAUSED:
                mediaPlayer.stop();
                setStatus(STATUS_STOPED);
                break;
            case STATUS_PLAYING:
                mediaPlayer.stop();
                setStatus(STATUS_STOPED);
                break;
            default:
                break;
        }
    }

    public static void stop() {
        if (null != mediaPlayer && isLoaded) {
            doStop();
        }
    }

    private static void doPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            setStatus(STATUS_PAUSED);
        }
    }

    public static void pause() {
        if (null != mediaPlayer && isLoaded) {
            doPause();
        }
    }

    private static void doRelease() {
        doStop();
        mediaPlayer.release();
        mediaPlayer = null;
        isLoaded = false;
    }

    public static void release() {
        if (null != mediaPlayer && isLoaded) {
            doRelease();
        }
    }

    private static void setStatus(int status) {
        mStatus = status;
    }

    public static void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        if (null != mediaPlayer) {
            mediaPlayer.setOnCompletionListener(onCompletionListener);
        }
    }
}
