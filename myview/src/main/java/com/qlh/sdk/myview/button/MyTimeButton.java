package com.qlh.sdk.myview.button;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.qlh.sdk.myview.R;
import com.qlh.sdk.myview.app.App;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 鉴于经常用到获取验证码倒计时按钮 在网上也没有找到理想的 自己写一个
 *
 * @author yung
 * <p>
 * 2015年1月14日[佛祖保佑 永无BUG]
 * <p>
 * PS: 由于发现timer每次cancle()之后不能重新schedule方法,所以计时完毕只恐timer.
 * 每次开始计时的时候重新设置timer, 没想到好办法初次下策
 * 注意把该类的onCreate()onDestroy()和activity的onCreate()onDestroy()同步处理
 */
public class MyTimeButton extends AppCompatButton implements OnClickListener {

    private final String TIME = "time";
    private final String CTIME = "ctime";
    private long length = 60;// 倒计时长度,这里给了默认60秒
    private String textAfter = "秒后重新获取";
    private String textBefore = "获取验证码";
    private OnClickListener mOnclickListener;
    private Timer t;
    private TimerTask task;
    private long time;
    @SuppressLint("HandlerLeak")
    private Handler han = new Handler() {
        public void handleMessage(android.os.Message msg) {
            MyTimeButton.this.setText(time / 1000 + textAfter);
            time -= 1000;
            if (time < 0) {
                MyTimeButton.this.setEnabled(true);
                MyTimeButton.this.setText(textBefore);
                clearTimer();
            }
        }

        ;
    };
    //private Map<String, Long> map;//主要用于上次没完成倒计时就退出活动，如果想实现保存，将map移至Application

    public MyTimeButton(Context context) {
        this(context, null);

    }

    public MyTimeButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public MyTimeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        handleStyleable(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l instanceof MyTimeButton) {
            super.setOnClickListener(l);
        } else
            this.mOnclickListener = l;
    }

    private void handleStyleable(Context context, AttributeSet attrs, int defStyle) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyTimeButton, defStyle, 0);

        textBefore = TextUtils.isEmpty(ta.getString(R.styleable.MyTimeButton_textBefore))
                ? textBefore
                : ta.getString(R.styleable.MyTimeButton_textBefore);

        textAfter = TextUtils.isEmpty(ta.getString(R.styleable.MyTimeButton_textAfter))
                ? textAfter
                : ta.getString(R.styleable.MyTimeButton_textAfter);

        length = (long) ta.getFloat(R.styleable.MyTimeButton_timeLength, length)* 1000;

        ta.recycle();

        //设置点击前文本
        this.setText(textBefore);
        if (getBackground() == null)//默认背景
            this.setBackgroundResource(R.drawable.time_button_bg);
    }

    @Override
    public void onClick(View v) {
        if (mOnclickListener != null)
            mOnclickListener.onClick(v);
        initTimer();
        this.setText(time / 1000 + textAfter);
        this.setEnabled(false);
        t.schedule(task, 0, 1000);
        // t.scheduleAtFixedRate(task, delay, period);
    }

    private void initTimer() {
        time = length;
        t = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                Log.e("yung", time / 1000 + "");
                han.sendEmptyMessage(0x01);
            }
        };
    }

    /**
     * 和activity的onDestroy()方法同步
     */
    public void onDestroy() {
        if (App.map == null)
            App.map = new HashMap<>();
        App.map.put(TIME, time);
        App.map.put(CTIME, System.currentTimeMillis());
        clearTimer();
        Log.e("yung", "onDestroy");
    }

    private void clearTimer() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (t != null)
            t.cancel();
        t = null;
    }

    /**
     * 和activity的onCreate()方法同步
     */
    public void onCreate(Bundle bundle) {
        Log.e("yung", App.map + "");
        if (App.map == null)
            return;
        if (App.map.size() <= 0)// 这里表示没有上次未完成的计时
            return;
        long time = System.currentTimeMillis() - App.map.get(CTIME)
                - App.map.get(TIME);
        App.map.clear();
        if (time > 0)
            return;
        else {
            initTimer();
            this.time = Math.abs(time);
            t.schedule(task, 0, 1000);
            this.setText(time + textAfter);
            this.setEnabled(false);
        }
    }

    /**
     * 设置计时时候显示的文本
     */
    public MyTimeButton setTextAfter(String text1) {
        this.textAfter = text1;
        return this;
    }

    /**
     * 设置点击之前的文本
     */
    public MyTimeButton setTextBefore(String text0) {
        this.textBefore = text0;
        this.setText(textBefore);
        return this;
    }

    /**
     * 设置到计时长度
     *
     * @param length 时间 默认毫秒
     * @return
     */
    public MyTimeButton setLength(long length) {
        this.length = length;
        return this;
    }
    /*

     *
     */
}