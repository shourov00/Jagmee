package com.jagmee.app.alivcsolution.view.video;

public interface OnUploadCompleteListener {
    void onSuccess(String videoId, String imageUrl,String videoDescribe);

    /**
     * 上传失败
     * @param code 错误码
     * @param msg 错误信息
     */
    void onFailure(String code, String msg);
}
