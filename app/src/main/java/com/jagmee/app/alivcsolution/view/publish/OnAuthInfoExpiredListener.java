package com.jagmee.app.alivcsolution.view.publish;

public interface OnAuthInfoExpiredListener {
    /**
     * 图片上传凭证过期
     */
    void onImageAuthInfoExpired();

    /**
     * 视频上传凭证过期
     */
    void onVideoAuthInfoExpired(String videoId);
}
