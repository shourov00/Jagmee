package com.aliyun.svideo.recorder.view.control;

public enum RecordState {
    /**
     * 录制状态，倒计时准备中
     */
    READY,
    /**
     * 录制中
     */
    RECORDING,
    /**
     * 尚未录制
     */
    STOP,
    /**
     * 倒计时录制中
     */
    COUNT_DOWN_RECORDING;
}
