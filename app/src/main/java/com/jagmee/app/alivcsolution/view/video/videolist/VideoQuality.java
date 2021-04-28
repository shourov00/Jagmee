package com.jagmee.app.alivcsolution.view.video.videolist;

public enum VideoQuality {
    /**
     *
     */
    DEFAULT("OD"),
    /**
     *
     */
    DOWNLOAD("LD"),
    /**
     *
     */
    PLAY("FD");

    VideoQuality(String value) {
        this.mValue = value;
    }
    private String mValue;

    public String getValue() {
        return mValue;
    }
}
