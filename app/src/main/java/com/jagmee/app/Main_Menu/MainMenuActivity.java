package com.jagmee.app.Main_Menu;

import android.Manifest;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aliyun.svideo.base.Constants;
import com.aliyun.svideo.common.utils.PermissionUtils;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.aliyun.svideo.recorder.util.RecordCommon;
import com.jagmee.app.Chat.Chat_Activity;
import com.jagmee.app.R;
import com.jagmee.app.SimpleClasses.Fragment_Callback;
import com.jagmee.app.SimpleClasses.Functions;
import com.jagmee.app.SimpleClasses.Variables;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jagmee.app.alivcsolution.constants.LittleVideoParamConfig;
import com.jagmee.app.alivcsolution.net.NetWatchdog;
import com.jagmee.app.alivcsolution.net.data.LittleImageUploadAuthInfo;
import com.jagmee.app.alivcsolution.net.data.LittleVideoUploadAuthInfo;
import com.jagmee.app.alivcsolution.sts.OnStsResultListener;
import com.jagmee.app.alivcsolution.sts.StsInfoManager;
import com.jagmee.app.alivcsolution.sts.StsTokenInfo;
import com.jagmee.app.alivcsolution.utils.Common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;


public class MainMenuActivity extends AppCompatActivity {
    public static MainMenuActivity mainMenuActivity;
    private MainMenuFragment mainMenuFragment;
    long mBackPressed;
    public static String token;
    public static Intent intent;

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private NetWatchdog netWatchdog;
    /**
     * 视频合成输出文件名称
     */
    private String mComposeFileName = "";
    /**
     * 视频合成输出文件路径
     */
    private String mComposeOutputPath = "";
    /**
     * 视频上传凭证信息
     */
    private LittleVideoUploadAuthInfo mVideoUploadAuthInfo;
    /**
     * 图片上传凭证信息
     */
    private LittleImageUploadAuthInfo mImageUploadAuthInfo;
    /**
     * 视频上传凭证信息获取成功
     */
    private boolean isVideoUploadAuthRequestSuccess = false;
    /**
     * 图片上传凭证信息获取成功
     */
    private boolean isImageUploadAuthRequestSuccess = false;
    /**
     * 权限列表
     */
    String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS
    };

    /**
     * assets目录文件拷贝工具类
     */
    private Common commonUtils;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        checkPermission();
        initPath();
        copyAssetsLogo();
        copyAssets();
        RecordCommon.copyRace(this);
        initNetWatchDog();

        mainMenuActivity=this;

        intent=getIntent();

        setIntent(null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Variables.screen_height= displayMetrics.heightPixels;
        Variables.screen_width= displayMetrics.widthPixels;

        Variables.sharedPreferences=getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

        Variables.user_id=Variables.sharedPreferences.getString(Variables.u_id,"");
        Variables.user_name=Variables.sharedPreferences.getString(Variables.u_name,"");
        Variables.user_pic=Variables.sharedPreferences.getString(Variables.u_pic,"");


        token= FirebaseInstanceId.getInstance().getToken();
        if(token==null || (token.equals("")||token.equals("null")))
            token=Variables.sharedPreferences.getString(Variables.device_token,"null");


        if (savedInstanceState == null) {

            initScreen();

        } else {
            mainMenuFragment = (MainMenuFragment) getSupportFragmentManager().getFragments().get(0);
        }

        Functions.make_directry(Variables.app_folder);
        Functions.make_directry(Variables.draft_app_folder);

    }


    private void checkPermission() {
        boolean checkResult = PermissionUtils.checkPermissionsGroup(this, permission);
        if (!checkResult) {
            PermissionUtils.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
        }
    }


    private void initPath(){
        mComposeFileName = System.currentTimeMillis() + "_output_compose_video.mp4";
        mComposeOutputPath = Constants.SDCardConstants.getDir(this) + LittleVideoParamConfig.DIR_COMPOSE + mComposeFileName;

        Log.e("mComposeOutputPath",mComposeOutputPath);
    }

    private void copyAssetsLogo() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("icon");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("icon/" + filename);
                File outFile = new File(getExternalFilesDir(""), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


    private void copyAssets() {
        commonUtils = Common.getInstance(getApplicationContext()).copyAssetsToSD("encrypt", "aliyun");
        commonUtils.setFileOperateCallback(

                new Common.FileOperateCallback() {
                    @Override
                    public void onSuccess() {
                        //AliyunDownloadConfig config = new AliyunDownloadConfig();
                        //config.setSecretImagePath(
                        //    Environment.getExternalStorageDirectory().getAbsolutePath() + "/aliyun/encryptedApp.dat");
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_save/");
                        if (!file.exists()) {
                            file.mkdir();
                        }
                    }

                    @Override
                    public void onFailed(String error) {
                        Log.e("Test", "unZip fail..");
                    }
                });
    }

    public void initNetWatchDog() {
        netWatchdog = new NetWatchdog(this);
        netWatchdog.setNetConnectedListener(new MyNetConnectedListener(this));
    }

    private static class MyNetConnectedListener implements NetWatchdog.NetConnectedListener {
        private WeakReference<MainMenuActivity> weakReference;

        MyNetConnectedListener(MainMenuActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onReNetConnected(boolean isReconnect) {
            if (isReconnect) {
                //刷新获取STS
                StsInfoManager.getInstance().refreshStsToken(new MyStsResultListener(weakReference.get()));
                //网络重连
                Log.e("Test", "onReNetConnected......");
            }
        }

        @Override
        public void onNetUnConnected() {
            MainMenuActivity videoListActivity = weakReference.get();
            if(videoListActivity != null){
                ToastUtils.show(videoListActivity,videoListActivity.getString(R.string.alivc_editor_more_no_network));
            }
            //网络断开
            Log.e("Test", "onNetUnConnected......");
        }
    }

    private static class MyStsResultListener implements OnStsResultListener {
        WeakReference<MainMenuActivity> weakReference;

        MyStsResultListener(MainMenuActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(StsTokenInfo tokenInfo) {

            MainMenuActivity videoListActivity = weakReference.get();
            // videoListActivity.videoPlayView刷新sts信息
            //   videoListActivity.videoPlayView.refreshStsInfo(tokenInfo);//..........................

        }

        @Override
        public void onFail() {

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        netWatchdog.startWatch();

    }

    @Override
    protected void onStop() {
        super.onStop();
        netWatchdog.stopWatch();

    }

    @Override
    protected void onDestroy() {
        if (commonUtils != null) {
            commonUtils.onDestroy();
            commonUtils = null;
        }
        super.onDestroy();
    }












    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if(intent!=null){
            String type=intent.getStringExtra("type");
            if(type!=null && type.equalsIgnoreCase("message")){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Chat_Activity chat_activity = new Chat_Activity(new Fragment_Callback() {
                            @Override
                            public void Responce(Bundle bundle) {

                            }
                        });
                        FragmentTransaction transaction = MainMenuActivity.mainMenuActivity.getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);

                        Bundle args = new Bundle();
                        args.putString("user_id", intent.getStringExtra("user_id"));
                        args.putString("user_name", intent.getStringExtra("user_name"));
                        args.putString("user_pic", intent.getStringExtra("user_pic"));

                        chat_activity.setArguments(args);
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
                    }
                },2000);

            }
        }

    }

    private void initScreen() {
        mainMenuFragment = new MainMenuFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainMenuFragment)
                .commit();

        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }






    @Override
    public void onBackPressed() {
        if (!mainMenuFragment.onBackPressed()) {
            int count = this.getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    Toast.makeText(getBaseContext(), "Tap Again To Exit", Toast.LENGTH_SHORT).show();
                    mBackPressed = System.currentTimeMillis();

                }
            } else {
                super.onBackPressed();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Functions.deleteCache(this);
    }


}
