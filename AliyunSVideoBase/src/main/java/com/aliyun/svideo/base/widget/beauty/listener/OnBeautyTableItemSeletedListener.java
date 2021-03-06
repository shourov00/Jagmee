package com.aliyun.svideo.base.widget.beauty.listener;

import com.aliyun.svideo.base.widget.beauty.enums.BeautyLevel;
import com.aliyun.svideo.base.widget.beauty.enums.BeautyMode;

public interface OnBeautyTableItemSeletedListener {
    /**
     * 美颜、美肌、美型tab选中监听
     * @param beautyMode 选中下标
     */
    void onNormalSelected( BeautyMode beautyMode);

}
