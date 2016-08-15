package com.catr.test.vrapp.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioTrack;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.catr.test.vrapp.R;
import com.catr.test.vrapp.adapter.PanoAdapter;
import com.catr.test.vrapp.utils.SdcardUtil;
import com.catr.test.vrapp.utils.VrFileUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Context mContext;
    private ListView panoInfoListView;
    private PanoAdapter mPanoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        panoInfoListView = (ListView) findViewById(R.id.lv_pano_infos);
        mPanoAdapter = new PanoAdapter(mContext, VrFileUtil.getVrPanoFileInfos());
        panoInfoListView.setAdapter(mPanoAdapter);

        panoInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, VrPanoramaActivity.class);
                intent.putExtra(VrApp.PANORAMA_NUM, position);
                mContext.startActivity(intent);
            }
        });
    }
}
