package com.aliyun.svideo.base.widget;

import android.view.View;

public interface SizeChangedNotifier {

    public interface Listener {
        void onSizeChanged(View view, int w, int h, int oldw, int oldh);
    }

    void setOnSizeChangedListener(Listener listener);

}
