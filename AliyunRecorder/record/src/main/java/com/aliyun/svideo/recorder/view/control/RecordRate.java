package com.aliyun.svideo.recorder.view.control;

public enum RecordRate {
    /**
     * 录制速度，很慢
     */
    VERY_FLOW(-8f),
    /**
     * 录制速度，较慢
     */
    FLOW(-4f),
    /**
     * 录制速度，标准
     */
    STANDARD(1f),
    /**
     * 录制速度，快
     */
    FAST(4f),
    /**
     * 录制速度，很快
     */
    VERY_FAST(8f);
    private float rate;

    private RecordRate(float rate) {
        this.rate = rate;
    }

    public float getRate() {
        return this.rate;
    }
}

