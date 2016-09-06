package com.catr.test.vrapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.catr.test.vrapp.R;
import com.catr.test.vrapp.model.VrPanoFileInfo;

import java.util.List;

/**
 * Created by Wony on 2016/7/29.
 */
public class PanoAdapter extends BaseAdapter {
    //全景照片文件信息列表
    private Context mContext;
    private List<VrPanoFileInfo> mVrPanoFileInfos;
    private LayoutInflater mInflater;

    public PanoAdapter(Context context, List<VrPanoFileInfo> vrPanoFileInfos) {
        this.mContext = context;
        this.mVrPanoFileInfos = vrPanoFileInfos;
        mInflater = LayoutInflater.from(mContext);
    }

    //改变数据源
//    public void changeData((List<VrPanoFileInfo> vrPanoFileInfos) {
//        this.mVrPanoFileInfos = vrPanoFileInfos;
//        notifyDataSetChanged();
//    }

    @Override
    public int getCount() {
        return mVrPanoFileInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mVrPanoFileInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.list_item_pano, null);
        }
        TextView panoTitle = (TextView)convertView.findViewById(R.id.tv_pano_title);
        TextView panoDes = (TextView)convertView.findViewById(R.id.tv_pano_description);
        VrPanoFileInfo vrPanoFileInfo = (VrPanoFileInfo)getItem(position);
        panoTitle.setText(vrPanoFileInfo.getFileTitle().toString());
        panoDes.setText(vrPanoFileInfo.getFileName().toString());
        return convertView;
    }
}
