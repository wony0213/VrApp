package com.catr.test.vrapp.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.catr.test.vrapp.Config;
import com.catr.test.vrapp.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wony on 2016/7/28.
 */
public class MediaUtil {
    private static final int STATUS_STOPED = 0;
    private static final int STATUS_PLAYING = 1;
    private static final int STATUS_PAUSED = 2;
    private int mStatus = STATUS_STOPED;

    private Context mContext;
    private Integer mediaResId;
    private String mediaPath;
    private MediaPlayer mediaPlayer = null;


    public void load(Context context, Integer mediaResId, String mediaPath) {
        this.mContext = context;
        this.mediaResId = mediaResId;
        this.mediaPath = mediaPath;

        //首先暂停音乐！
//        this.stop();

        if (Config.versionFlag == Config.SDCARD_VERSION && null != mediaPath) {
            mediaPlayer = MediaPlayer.create(mContext, Uri.parse("file://" + mediaPath));
        } else if (Config.versionFlag == Config.ASSETS_VERSION && null != mediaResId) {
            mediaPlayer = MediaPlayer.create(mContext, mediaResId.intValue());
        } else {
            mediaPlayer = null;
        }
    }

    private boolean doPlay() {
        switch (mStatus) {
            case STATUS_STOPED:
                //音乐播放机制未完全搞明白，以下代码会报错
//                try {
//                    mediaPlayer.reset();
//                    mediaPlayer.prepare();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return false;
//                }
                if (null != mediaPath) {
                    mediaPlayer = MediaPlayer.create(mContext, Uri.parse("file://" + mediaPath));
                } else {
                    mediaPlayer = MediaPlayer.create(mContext, mediaResId.intValue());
                }
                mediaPlayer.start();
                break;
            case STATUS_PAUSED:
                mediaPlayer.start();
                break;
            case STATUS_PLAYING:
                break;
            default:
                break;
        }
        return true;
    }

    public boolean play() {
        if (null != mediaPlayer) {
            return doPlay();
        } else {
            return false;
        }
    }

    private void doStop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            setStatus(STATUS_STOPED);
        }
    }

    public void stop() {
        if (null != mediaPlayer) {
            doStop();
        }
    }

    private void doPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            setStatus(STATUS_PAUSED);
        }
    }

    public void pause() {
        if (null != mediaPlayer) {
            doPause();
        }
    }

    private void doRelease() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void release() {
        if (null != mediaPlayer) {
            doRelease();
        }
    }

    private void setStatus(int status) {
        mStatus = status;
    }
}
