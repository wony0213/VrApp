package com.catr.test.vrapp.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.catr.test.vrapp.R;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 1 on 2016/8/31.
 */
public class PanoListAdapter extends BaseAdapter{
    private Context mContext;
    private int mCurrentItem=0;
    final int TYPE_1 = 0;
    final int TYPE_2 = 1;
    private String[] mPlaceName = {"故宫", "九寨沟", "卡帕热气球", "白宫", "白金汉宫","威尼斯水城", "城山日出峰", "富士山"};
    private String[] mCountry = {"中国", "中国", "土耳其", "美国", "英国","意大利", "韩国","日本"};
    private String[] mCity = {"北京", "四川", "卡帕多西亚", "哥伦比亚特区", "伦敦","威尼斯", "济州特别自治道","静冈"};
    private int[] mImgs = {R.drawable.gugong,R.drawable.jiuzhaigou,R.drawable.kapareqiqiu,R.drawable.baigong,
    R.drawable.baijinhan,R.drawable.weinisi,R.drawable.chengshanrichufeng,R.drawable.fushishan};
    private String[] mPanoImgs={"caict-mono-1.jpg","lab_ten_floor-mono-2.jpg","first_floor_moniwangshiyanshiB-mono-3.jpg",
            "third_floor_apkudoshiyanshi-mono-4.jpg","third_floor_zidonghuaceshishiyanshiA-mono-5.jpg","third_floor_jixieshoushiyanshi-mono-6.jpg",
            "ten_floor_jixieshoushiyanshi-mono-7.jpg","waijingzhulou-mono-1.jpg"};
    InputStream istr = null;
    public PanoListAdapter(Context context){
        mContext=context;
    }
    @Override
    public int getCount() {
        return mPlaceName.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 viewHolder1=null;
        ViewHolder2 viewHolder2=null;
        int type = getItemViewType(position);

        if(convertView==null){
            if(type==TYPE_1){
                viewHolder1 = new ViewHolder1();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_video,null);
                viewHolder1.mPlaceNameTv1 = (TextView) convertView.findViewById(R.id.placeName_tv1);
                viewHolder1.mCountryTv1 = (TextView) convertView.findViewById(R.id.country_tv1);
                viewHolder1.mCity1 = (TextView)  convertView.findViewById(R.id.city_tv1);
                viewHolder1.mImgView = (ImageView) convertView.findViewById(R.id.view_iv);
                convertView.setTag(viewHolder1);
            }else if(type==TYPE_2){
                viewHolder2 = new ViewHolder2();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_panoview,null);
                viewHolder2.mPlaceNameTv2 = (TextView) convertView.findViewById(R.id.placeName_tv2);
                viewHolder2.mCountryTv2 = (TextView) convertView.findViewById(R.id.country_tv2);
                viewHolder2.mCity2 = (TextView)  convertView.findViewById(R.id.city_tv2);
                viewHolder2.mPanoView = (VrPanoramaView) convertView.findViewById(R.id.pano_view_list);
                viewHolder2.mTempImgView=(ImageView) convertView.findViewById(R.id.tempview_iv) ;
                convertView.setTag(viewHolder2);
            }

        }else{
            if (type == TYPE_1) {
                viewHolder1 = (ViewHolder1) convertView.getTag();
            } else if (type == TYPE_2) {
                viewHolder2 = (ViewHolder2) convertView.getTag();
            }
        }
        if (type == TYPE_1) {
            viewHolder1.mPlaceNameTv1.setText(mPlaceName[position]);
            viewHolder1.mCountryTv1.setText(mCountry[position]);
            viewHolder1.mCity1.setText(mCity[position]);
            viewHolder1.mImgView.setBackgroundResource(mImgs[position]);
        } else if (type == TYPE_2) {
            viewHolder2.mPlaceNameTv2.setText(mPlaceName[position]);
            viewHolder2.mCountryTv2.setText(mCountry[position]);
            viewHolder2.mCity2.setText(mCity[position]);
            viewHolder2.mTempImgView.setVisibility(View.VISIBLE);
            viewHolder2.mTempImgView.setBackgroundResource(mImgs[position]);
            viewHolder2.mPanoView.setVisibility(View.GONE);
            viewHolder2.mPanoView.setInfoButtonEnabled(false);
            viewHolder2.mPanoView.setFullscreenButtonEnabled(false);
            viewHolder2.mPanoView.setStereoModeButtonEnabled(false);
            PanoViewLoaderTask mPanoViewLoaderTask=new PanoViewLoaderTask(mContext,viewHolder2.mPanoView,viewHolder2.mTempImgView,mPanoImgs[position]);
            mPanoViewLoaderTask.execute();
        }


        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mCurrentItem) {
            return TYPE_2;
        } else {
            return TYPE_1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
    public void setCurrentItem(int currentItem) {
        this.mCurrentItem = currentItem;
    }
    class ViewHolder1{
       TextView  mPlaceNameTv1;
        TextView  mCountryTv1;
        TextView mCity1;
        ImageView mImgView;
    }
    class ViewHolder2{
        TextView  mPlaceNameTv2;
        TextView  mCountryTv2;
        TextView mCity2;
        VrPanoramaView mPanoView;
        ImageView mTempImgView;
    }
    public class PanoViewLoaderTask extends AsyncTask<String, Void, Boolean> {
        private Context mContext;
        private VrPanoramaView mVrPanoramaView;
        private String mPanoImgName;
        private ImageView mTempView;
        public PanoViewLoaderTask(Context context, VrPanoramaView vrPanoramaView,ImageView tempView,String panoImgName){
            mContext=context;
            mVrPanoramaView=vrPanoramaView;
            mPanoImgName=panoImgName;
            mTempView=tempView;
        }

        /**
         * Reads the bitmap from disk in the background and waits until it's loaded by pano widget.
         */
        @Override
        protected Boolean doInBackground(String... params) {
            InputStream istr = null;
            VrPanoramaView.Options panoOptions = null;
            if (null != mPanoImgName ) {
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

            mVrPanoramaView.loadImageFromBitmap(BitmapFactory.decodeStream(istr),panoOptions);


            try {
                istr.close();
            } catch (IOException e) {
                Log.e("PanoViewLoader", "Could not close input stream: " + e);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSucLoad) {
            super.onPostExecute(isSucLoad);
            if(isSucLoad){
                mVrPanoramaView.setVisibility(View.VISIBLE);
                mTempView.setVisibility(View.GONE);
            }

        }
    }
}
