package com.catr.test.vrapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import com.catr.test.vrapp.R;

/**
 * Created by 1 on 2016/9/14.
 */
public class DetailActivity extends Activity {
    private TextView mTextView;   //文本域
    private ImageView mImageView;  //翻转icon
    int maxLine = 3;  //TextView设置默认最大展示行数为5

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        mTextView = (TextView) findViewById(R.id.detail_text);
        mImageView = (ImageView) findViewById(R.id.turn_over_icon);

        mTextView.setText(R.string.paragraph1);

        mTextView.setHeight(mTextView.getLineHeight() * maxLine);  //设置默认显示高度

       // 根据高度来控制是否展示翻转icon
        mTextView.post(new Runnable() {
            @Override
            public void run() {
                mImageView.setVisibility(mTextView.getLineCount() > maxLine ? View.VISIBLE : View.GONE);
                Log.e("Test", mTextView.getLineCount() + "aaaaaaa");
            }
        });

        mImageView.setOnClickListener(new MyTurnListener());  //翻转监听
    }


    private class MyTurnListener implements View.OnClickListener {

        boolean isExpand;  //是否翻转

        @Override
        public void onClick(View v) {
            isExpand = !isExpand;
            mTextView.clearAnimation();  //清除动画
            final int tempHight;
            final int startHight = mTextView.getHeight();  //起始高度
            int durationMillis = 200;

            if (isExpand) {
                /**
                 * 折叠效果，从长文折叠成短文
                 */

                tempHight = mTextView.getLineHeight() * mTextView.getLineCount() - startHight;  //为正值，长文减去短文的高度差
                //翻转icon的180度旋转动画
                RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(durationMillis);
                animation.setFillAfter(true);
                mImageView.startAnimation(animation);
            } else {
                /**
                 * 展开效果，从短文展开成长文
                 */
                tempHight = mTextView.getLineHeight() * maxLine - startHight;//为负值，即短文减去长文的高度差
                //翻转icon的180度旋转动画
                RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(durationMillis);
                animation.setFillAfter(true);
                mImageView.startAnimation(animation);
            }

            Animation animation = new Animation() {
                //interpolatedTime 为当前动画帧对应的相对时间，值总在0-1之间
                protected void applyTransformation(float interpolatedTime, Transformation t) { //根据ImageView旋转动画的百分比来显示textview高度，达到动画效果
                    mTextView.setHeight((int) (startHight + tempHight * interpolatedTime));//原始长度+高度差*（从0到1的渐变）即表现为动画效果

                }
            };
            animation.setDuration(durationMillis);
            mTextView.startAnimation(animation);

        }
    }
}
