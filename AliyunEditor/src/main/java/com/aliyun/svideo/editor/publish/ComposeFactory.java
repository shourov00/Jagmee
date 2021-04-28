package com.aliyun.svideo.editor.publish;

import com.aliyun.qupai.editor.impl.AliyunComposeFactory;
import com.aliyun.qupai.editor.impl.AliyunVodCompose;

public enum ComposeFactory {
    /**
     * 合成及上传
     */
    INSTANCE;

    private AliyunVodCompose aliyunVodCompose;
    ComposeFactory() {
        aliyunVodCompose = AliyunComposeFactory.createAliyunVodCompose();
    }

    public AliyunVodCompose getAliyunVodCompose() {
        return aliyunVodCompose;
    }
}
