package com.aliyun.svideo.editor.effects.control;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;

public class TabGroup implements OnClickListener {
    private static final long MIN_CLICK_INTERVAL = 1000;
    public interface OnCheckedChangeListener {
        void onCheckedChanged(TabGroup control, int checkedIndex);
    }

    private OnCheckedChangeListener mOnCheckedChangeistener;
    private OnTabChangeListener mOnTabChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeistener = listener;
    }

    public void setOnTabChangeListener(OnTabChangeListener listener) {
        mOnTabChangeListener = listener;
    }

    private final ArrayList<View> mViewList = new ArrayList<>();

    public void addView(View view) {
        view.setOnClickListener(this);
        mViewList.add(view);
    }

    private int mCheckedIndex = -1;

    public int getCheckedIndex() {
        return mCheckedIndex;
    }
    public void setCheckedView(View item) {

        setCheckedIndex(mViewList.indexOf(item));
    }

    public void setCheckedIndex(int index) {
        if (mCheckedIndex >= 0) {
            mViewList.get(mCheckedIndex).setActivated(false);
        }
        mCheckedIndex = index;
        if (mCheckedIndex >= 0) {
            mViewList.get(mCheckedIndex).setActivated(true);
            /*if (mCheckedIndex == 4 || mCheckedIndex == 5 || mCheckedIndex == 6 || mCheckedIndex == 9 || mCheckedIndex == 10 || mCheckedIndex == 11 || mCheckedIndex == 12) {
                mViewList.get(mCheckedIndex).setActivated(false);
            } else {
                mViewList.get(mCheckedIndex).setActivated(true);
            }*/
        }
        if (mOnCheckedChangeistener != null) {
            mOnCheckedChangeistener.onCheckedChanged(this, mCheckedIndex);
        }
    }

    @Override
    public void onClick(View v) {
        if (isTimeEnabled()) {
            setCheckedView(v);
            if (mOnTabChangeListener != null) {
                mOnTabChangeListener.onTabChange();
            }
        }

    }

    private long lastTimeMillis;

    /**
     * ????????????????????????????????????????????????????????????
     * @return
     */
    protected boolean isTimeEnabled() {
        long currentTimeMillis = System.currentTimeMillis();
        if ((currentTimeMillis - lastTimeMillis) > MIN_CLICK_INTERVAL) {
            lastTimeMillis = currentTimeMillis;
            return true;
        }
        return false;
    }


}
