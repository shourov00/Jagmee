package com.aliyun.svideo.base.widget.beauty.seekbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.aliyun.svideo.base.R;
import com.aliyun.svideo.base.widget.beauty.listener.OnProgresschangeListener;


public class BeautySeekBar extends FrameLayout {
    private Context mContext;
    /**
     * 拖拽seek
     */
    private IndicatorSeekBar mFrontSeekBar;
    /**
     * 菱形标记
     */
    private SeekBar mBackSeekBar;

    private boolean hasHistory;

    private boolean isSeekBarNegativeMin = false;

    public BeautySeekBar(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public BeautySeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public BeautySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public void setProgress(int progress) {
        mFrontSeekBar.setProgress(progress);
        //mBackSeekBar.setProgress(progress);
        mBackSeekBar.setVisibility(View.VISIBLE);
    }

    public void setLastProgress(float progress) {
        hasHistory = true;
        //mBackSeekBar.setProgress(progress);
        mFrontSeekBar.setProgress(progress);
        mBackSeekBar.setVisibility(View.VISIBLE);
    }

    public void setSeekIndicator(float progress) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBackSeekBar.setProgress((int)progress);
        } else if (isSeekBarNegativeMin) {
            mBackSeekBar.setProgress((int)progress + 100);
        } else {
            mBackSeekBar.setProgress((int)progress);
        }
    }

    public int getSeekIndicator() {
        return mBackSeekBar.getProgress();
    }

    public void resetProgress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mFrontSeekBar.setProgress(mBackSeekBar.getProgress());
        } else if (isSeekBarNegativeMin) {
            mFrontSeekBar.setProgress(mBackSeekBar.getProgress() - 100);
        } else {
            mFrontSeekBar.setProgress(mBackSeekBar.getProgress());
        }

    }

    private OnProgresschangeListener mListener;

    public void setProgressChangeListener(OnProgresschangeListener listener) {
        mListener = listener;
    }

    public void setMin(float min) {
        mFrontSeekBar.setMin(min);
    }
    public void setBackSeekMin(int min) {
        if (min >= 0) {
            isSeekBarNegativeMin = false;
        } else {
            isSeekBarNegativeMin = true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBackSeekBar.setMin(min);
        } else if (isSeekBarNegativeMin) {
            mBackSeekBar.setMax(200);
        } else {
            mBackSeekBar.setMax(100);
        }
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.alivc_beauty_seekbar, this);
        mFrontSeekBar = findViewById(R.id.front_seekbar);
        mFrontSeekBar.setIndicatorGap(10);

        mBackSeekBar = findViewById(R.id.back_seekbar);

        mBackSeekBar.setEnabled(false);
        mBackSeekBar.setActivated(false);

        mFrontSeekBar.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
                if (mListener != null) {
                    mListener.onProgressChange(progress);
                }
            }

            @Override
            public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });
    }
}
