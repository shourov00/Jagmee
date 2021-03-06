package com.aliyun.svideo.recorder.view.effects.filter.animfilter;

import com.aliyun.svideo.sdk.external.struct.effect.EffectFilter;

public interface OnAnimFilterItemClickListener {
    /**
     * 特效滤镜item点击
     * @param effectInfo 特效对象
     * @param index 下标
     */
    void onItemClick(EffectFilter effectInfo, int index);
}
