package com.jagmee.app.alivcsolution.sts;

public interface OnStsResultListener {
    void onSuccess(StsTokenInfo tokenInfo);
    void onFail();
}
