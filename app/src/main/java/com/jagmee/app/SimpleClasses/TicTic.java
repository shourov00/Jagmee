package com.jagmee.app.SimpleClasses;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.aliyun.private_service.PrivateService;
import com.aliyun.svideo.downloader.DownloaderManager;
import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;


/**
 * Created by AQEEL on 3/18/2019.
 */

public class TicTic extends Application {

    public static String shareurl;
    public static String downloadurl;
    public static String videoId;
    public static String videoUsername;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        FirebaseApp.initializeApp(this);
        AudienceNetworkAds.initialize(this);

        initDownLoader();

    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        TicTic app = (TicTic) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)
                .maxCacheFilesCount(20)
                .build();
    }


    private void initDownLoader() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aliyun/encryptedApp.dat";
        PrivateService.initService(this, filePath );
        DownloaderManager.getInstance().init(this);

    }

}
