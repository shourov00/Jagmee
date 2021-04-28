package com.jagmee.app.alivcsolution.view.video;

import com.jagmee.app.alivcsolution.sts.StsTokenInfo;

public interface OnStsInfoExpiredListener {
    void onTimeExpired();

    /**
     * 重新刷新并获取sts信息，需要在子线程调用，并且外部进行同步获取sts信息
     * @return 从网络重新获取的sts信息
     */
    StsTokenInfo refreshSts();
}
