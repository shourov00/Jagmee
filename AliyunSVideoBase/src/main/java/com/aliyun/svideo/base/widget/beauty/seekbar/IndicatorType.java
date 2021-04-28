package com.aliyun.svideo.base.widget.beauty.seekbar;

public interface IndicatorType {
    /**
     * the indicator corners is square shape
     */
    int RECTANGLE = 0;
    /**
     * the indicator corners is rounded shape
     */
    int RECTANGLE_ROUNDED_CORNER = 1;
    /**
     * the indicator shape like water-drop
     */
    int CIRCULAR_BUBBLE = 2;
    /**
     * set custom indicator you want.
     */
    int CUSTOM = 3;

}