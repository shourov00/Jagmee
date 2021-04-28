package com.aliyun.svideo.recorder.view.effects.filter;

public interface OnFilterItemClickListener {
    /**
     * 滤镜item点击
     * @param effectInfo 特效对象
     * @param index 下标
     * @return
     */
    void onItemClick(EffectInfo effectInfo, int index);
}
