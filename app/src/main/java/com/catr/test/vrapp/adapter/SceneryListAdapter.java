package com.catr.test.vrapp.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.catr.test.vrapp.R;
import com.catr.test.vrapp.model.SceneryInfo;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Wony on 2016/9/6.
 */
public class SceneryListAdapter extends BaseAdapter {

    private static final String TAG = "SceneryListAdapter";

    public static int SCROLL_STATE_IDLE = 0;
    public static int SCROLL_STATE_TOUCH_SCROLL = 1;
    public static int SCROLL_STATE_FLING = 2;

    private Context mContext;
    private int mCurrentItem = 0;
    private int mScrollState = SCROLL_STATE_IDLE;

    private List<SceneryInfo> mSceneryInfos;

    private static TranslateAnimation mHiddenAnimation;
    static {
        mHiddenAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        mHiddenAnimation.setDuration(500);
    }

    public SceneryListAdapter(Context context, List<SceneryInfo> sceneryInfos) {
        this.mContext = context;
        this.mSceneryInfos = sceneryInfos;
    }

    @Override
    public int getCount() {
        return mSceneryInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mSceneryInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.e("Test", "getView()" + position);

        ViewHolder ViewHolder;

        if (convertView == null) {
            ViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_scenery, null);
            ViewHolder.mSceneryNameTv = (TextView) convertView.findViewById(R.id.placeName_tv2);
            ViewHolder.mCountryTv = (TextView) convertView.findViewById(R.id.country_tv2);
            ViewHolder.mCity = (TextView) convertView.findViewById(R.id.city_tv2);
            ViewHolder.mPanoView = (VrPanoramaView) convertView.findViewById(R.id.pano_view_list);
            ViewHolder.mCoverImgView = (ImageView) convertView.findViewById(R.id.tempview_iv);
            convertView.setTag(ViewHolder);
        } else {
            ViewHolder = (ViewHolder) convertView.getTag();
        }
        SceneryInfo sceneryInfo = (SceneryInfo) getItem(position);
        ViewHolder.mSceneryNameTv.setText(sceneryInfo.getSceneryName());
        ViewHolder.mCountryTv.setText(sceneryInfo.getCountry());
        ViewHolder.mCity.setText(sceneryInfo.getCity());
        ViewHolder.mCoverImgView.setVisibility(View.VISIBLE);
        ViewHolder.mCoverImgView.setBackgroundResource(sceneryInfo.getPicResId());

        if (position == mCurrentItem && mScrollState == SCROLL_STATE_IDLE) {
            Log.e("Test", "显示panoview" + position);
            ViewHolder.mPanoView.setVisibility(View.VISIBLE);
            ViewHolder.mPanoView.setInfoButtonEnabled(false);
            ViewHolder.mPanoView.setFullscreenButtonEnabled(false);
            ViewHolder.mPanoView.setStereoModeButtonEnabled(false);
            ViewHolder.mPanoView.setEventListener(new PanoEventListener(ViewHolder.mCoverImgView));

            PanoViewLoaderTask mPanoViewLoaderTask = new PanoViewLoaderTask(mContext, ViewHolder.mPanoView, ViewHolder.mCoverImgView, sceneryInfo.getPanoName());
            mPanoViewLoaderTask.execute();
        }

        return convertView;
    }

    public void refresh() {

    }

    /**
     * Listen to the important events from widget.
     */
    private class PanoEventListener extends VrPanoramaEventListener {
        private View mCoverView;

        public PanoEventListener(View mCoverView) {
            this.mCoverView = mCoverView;
        }

        /**
         * Called by pano widget on the UI thread when it's done loading the image.
         */
        @Override
        public void onLoadSuccess() {
            mCoverView.startAnimation(mHiddenAnimation);
            mCoverView.setVisibility(View.INVISIBLE);
        }

        /**
         * Called by pano widget on the UI thread on any asynchronous error.
         */
        @Override
        public void onLoadError(String errorMessage) {

        }

        @Override
        public void onDisplayModeChanged(int newDisplayMode) {

        }

        @Override
        public void onClick() {

        }
    }

    class ViewHolder {
        TextView mSceneryNameTv;
        TextView mCountryTv;
        TextView mCity;
        VrPanoramaView mPanoView;
        ImageView mCoverImgView;
    }

    public void setCurrentItem(int currentItem) {
        this.mCurrentItem = currentItem;
    }

    public int getCurrentItem() {
        return mCurrentItem;
    }

    public void setScrollState(int scrollState) {
        this.mScrollState = scrollState;
    }

    public int getScrollState() {
        return mScrollState;
    }


    public class PanoViewLoaderTask extends AsyncTask<String, Void, Boolean> {
        private Context mContext;
        private VrPanoramaView mVrPanoramaView;
        private String mPanoImgName;
        private ImageView mTempView;

        public PanoViewLoaderTask(Context context, VrPanoramaView vrPanoramaView, ImageView tempView, String panoImgName) {
            mContext = context;
            mVrPanoramaView = vrPanoramaView;
            mPanoImgName = panoImgName;
            mTempView = tempView;
        }

        /**
         * Reads the bitmap from disk in the background and waits until it's loaded by pano widget.
         */
        @Override
        protected Boolean doInBackground(String... params) {
            InputStream istr = null;
            VrPanoramaView.Options panoOptions = null;
            if (null != mPanoImgName) {
//

                AssetManager assetManager = mContext.getAssets();
                try {
                    istr = assetManager.open(mPanoImgName);
                    panoOptions = new VrPanoramaView.Options();
                    //panoOptions.inputType = Options.TYPE_STEREO_OVER_UNDER;
                    panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
                } catch (IOException e) {
                    Log.e("PanoViewLoader", "Could not decode default bitmap: " + e);
                    return false;
                }
            }

            mVrPanoramaView.loadImageFromBitmap(BitmapFactory.decodeStream(istr), panoOptions);


            try {
                istr.close();
            } catch (IOException e) {
                Log.e("PanoViewLoader", "Could not close input stream: " + e);
            }
            return true;
        }

//        @Override
//        protected void onPostExecute(Boolean isSucLoad) {
//            super.onPostExecute(isSucLoad);
//            if (isSucLoad) {
//                mVrPanoramaView.setVisibility(View.VISIBLE);
//                mTempView.setVisibility(View.GONE);
//            }
//
//        }
    }
}
