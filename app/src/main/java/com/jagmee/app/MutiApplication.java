package com.jagmee.app;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.multidex.MultiDex;

import com.aliyun.common.httpfinal.QupaiHttpFinal;
import com.aliyun.private_service.PrivateService;
import com.aliyun.svideo.downloader.DownloaderManager;
import com.aliyun.sys.AlivcSdkCore;
import com.jagmee.app.alivcsolution.net.LittleHttpManager;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

public class MutiApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
            }
        });

        initHttp();
        initDownLoader();
        //短视频sdk，暂时只支持api 18以上的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            AlivcSdkCore.register(getApplicationContext());
            AlivcSdkCore.setLogLevel(AlivcSdkCore.AlivcLogLevel.AlivcLogDebug);

        }

        LittleHttpManager.getInstance().init(this);
    }

    /**
     * 短视频需要的http依赖
     */
    private void initHttp() {
        QupaiHttpFinal.getInstance().initOkHttpFinal();
    }
    private void initDownLoader() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aliyun/encryptedApp.dat";
        PrivateService.initService(this, filePath );
        DownloaderManager.getInstance().init(this);

    }


}
