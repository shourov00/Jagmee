package com.aliyun.svideo.recorder.view.control;

public enum FlashType {
    /**
     * 关闭闪光灯
     */
    OFF("off"),
    /**
     * 只闪一下
     */
    ON("on"),
    /**
     * 自动
     */
    AUTO("auto"),
    /**
     * 持续亮灯
     */
    TORCH("torch");

    private String type;

    private FlashType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
