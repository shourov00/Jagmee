package com.jagmee.app.alivcsolution.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aliyun.crop.AliyunCropCreator;
import com.aliyun.crop.supply.AliyunICrop;
import com.aliyun.editor.EditorCallBack;
import com.aliyun.qupai.editor.AliyunIEditor;
import com.aliyun.qupai.editor.AliyunPasterController;
import com.aliyun.qupai.editor.AliyunPasterManager;
import com.aliyun.qupai.editor.OnAnimationFilterRestored;
import com.aliyun.qupai.editor.OnPasterRestored;
import com.aliyun.qupai.editor.impl.AliyunEditorFactory;
import com.aliyun.qupai.import_core.AliyunIImport;
import com.aliyun.qupai.import_core.AliyunImportCreator;
import com.aliyun.svideo.base.AlivcEditorRoute;
import com.aliyun.svideo.editor.effects.filter.AnimationFilterController;
import com.aliyun.svideo.editor.util.FixedToastUtils;
import com.aliyun.svideo.media.MediaInfo;
import com.aliyun.svideo.sdk.external.struct.common.AliyunImageClip;
import com.aliyun.svideo.sdk.external.struct.common.AliyunVideoClip;
import com.aliyun.svideo.sdk.external.struct.common.AliyunVideoParam;
import com.aliyun.svideo.sdk.external.struct.common.VideoDisplayMode;
import com.aliyun.svideo.sdk.external.struct.effect.EffectFilter;
import com.jagmee.app.R;

import java.io.File;
import java.util.List;

import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;

public class AlivcLittlePreviewActivity extends AppCompatActivity implements OnAnimationFilterRestored {
    public static final String KEY_PARAM_CONFIG = "project_json_path";
    public static final String KEY_PARAM_THUMBNAIL = "svideo_thumbnail";
    public static final String KEY_PARAM_DESCRIBE = "svideo_describe";
    public static final String KEY_PARAM_VIDEO_RATIO = "key_param_video_ratio";
    private static final String KEY_PARAM_VIDEO_PARAM = "videoParam";

    private SurfaceView mSurfaceView;
    private int mScreenWidth;
    private Uri mUri;
    private AliyunIEditor mAliyunIEditor;
    private AliyunVideoParam mVideoParam;
    private float videoRatio;
    private Toast showToast;
    private AliyunPasterManager mPasterManager;

    /**
     * ??????
     */
    private int mVolume = 50;

    private OnPasterRestored mOnPasterRestoreListener = new OnPasterRestored() {
        @Override
        public void onPasterRestored(List<AliyunPasterController> list) {

        }
    };
    private AnimationFilterController mAnimationFilterController;
    private AliyunICrop mTranscoder;
    private String mConfigPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_little_activity_publish_preview);
        initData();
        initEditor();

        findViewById(R.id.iv_preview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        videoRatio = intent.getFloatExtra(KEY_PARAM_VIDEO_RATIO, 0f);
        mConfigPath = intent.getStringExtra(KEY_PARAM_CONFIG);
        mVideoParam = (AliyunVideoParam)intent.getSerializableExtra(KEY_PARAM_VIDEO_PARAM);

        if (mConfigPath != null) {
            mUri = Uri.fromFile(new File(mConfigPath));
        } else {
            List<MediaInfo> mediaInfos = intent.getParcelableArrayListExtra(AlivcEditorRoute.KEY_INTENT_MEDIA_INFO);
            mUri = Uri.fromFile(new File(getProjectJsonPath(mediaInfos)));
        }
    }

    /**
     * ??????MediaInfo??????ProjectJson
     *
     * @param mediaInfos List<MediaInfo>
     * @return jsonPath
     */
    private String getProjectJsonPath(List<MediaInfo> mediaInfos) {

        AliyunIImport mImport = AliyunImportCreator.getImportInstance(this);
        mImport.setVideoParam(mVideoParam);
        int size = mediaInfos.size();
        for (int i = 0; i < size; i++) {
            MediaInfo mediaInfo = mediaInfos.get(i);
            if (mediaInfo.mimeType.startsWith("video")) {
                mImport.addMediaClip(new AliyunVideoClip.Builder()
                        .source(mediaInfo.filePath)
                        .startTime(mediaInfo.startTime)
                        .endTime(mediaInfo.startTime + mediaInfo.duration)
                        .build());
            } else if (mediaInfo.mimeType.startsWith("image")) {
                mImport.addMediaClip(new AliyunImageClip.Builder()
                        .source(mediaInfo.filePath)
                        .duration(mediaInfo.duration)
                        .build());
            }
        }

        return mImport.generateProjectConfigure();
    }

    private void initEditor() {
        mAliyunIEditor = AliyunEditorFactory.creatAliyunEditor(mUri, mEditorCallback);
        Display defaultDisplay = getWindow().getWindowManager().getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();

        initSufaceView();
        if (videoRatio > 0) {
            height = getSurfaceHeight(width);
        }
        ViewGroup.LayoutParams layoutParams = mSurfaceView.getLayoutParams();
        layoutParams.width =  width;
        layoutParams.height = height;
        mSurfaceView.setLayoutParams(layoutParams);
        {
            //?????????????????????????????????AliyunIEditor.init??????????????????????????????????????????????????????UI?????????????????????????????????????????????????????????????????????UI
            mPasterManager = mAliyunIEditor.createPasterManager();
            /*
              ???????????????????????????????????????mPasterManager.setDisplaySize??????????????????????????????????????????????????????????????????????????????????????????????????????
              ?????????????????????????????????wrapContent??????matchParent?????????????????????????????????view??????????????????????????????
             */
            try {
                mPasterManager.setDisplaySize(width, height);
            } catch (Exception e) {
                showToast = FixedToastUtils.show(this, e.getMessage());
                finish();
            }
            mPasterManager.setOnPasterRestoreListener(mOnPasterRestoreListener);
            mAnimationFilterController = new AnimationFilterController(this.getApplicationContext(),
                    mAliyunIEditor);
            mAliyunIEditor.setAnimationRestoredListener(this);
        }

        mTranscoder = AliyunCropCreator.createCropInstance(this);
        VideoDisplayMode mode = mVideoParam.getScaleMode();
        int ret = mAliyunIEditor.init(mSurfaceView, this);
        mAliyunIEditor.setDisplayMode(mode);
        mAliyunIEditor.setVolume(mVolume);
        mAliyunIEditor.setFillBackgroundColor(Color.BLACK);
        mAliyunIEditor.play();

    }

    private void initSufaceView() {
        mSurfaceView = findViewById(R.id.alivc_ittle_preview);


    }

    private EditorCallBack mEditorCallback = new EditorCallBack() {
        @Override
        public void onEnd(int state) {
            // ????????????
            AlivcLittlePreviewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAliyunIEditor != null) {
                        mAliyunIEditor.replay();
                    }
                }
            });
        }

        @Override
        public void onError(final int errorCode) {
            // ????????????

        }

        @Override
        public int onCustomRender(int srcTextureID, int width, int height) {
            // ????????????
            return srcTextureID;
        }

        @Override
        public int onTextureRender(int srcTextureID, int width, int height) {
            // ???????????????
            return srcTextureID;
        }

        @Override
        public void onPlayProgress(long l, long l1) {
            // ???????????????????????????????????????????????????10s????????????????????????15s???????????????15s??????currentPlayTime=15s, currentStreamPlayTime=10s
        }

        @Override
        public void onDataReady() {
            // ??????????????????????????????
            AlivcLittlePreviewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAliyunIEditor != null) {
                        mAliyunIEditor.resume();
                    }

                }
            });
        }
    };

    @Override
    public void animationFilterRestored(List<EffectFilter> list) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KEYCODE_VOLUME_DOWN:
                mVolume -= 5;
                if (mVolume < 0) {
                    mVolume = 0;
                }
                if (mAliyunIEditor != null) {
                    mAliyunIEditor.setVolume(mVolume);
                }
                return true;
            case KEYCODE_VOLUME_UP:
                mVolume += 5;
                if (mVolume > 100) {
                    mVolume = 100;
                }
                if (mAliyunIEditor != null) {
                    mAliyunIEditor.setVolume(mVolume);
                }
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAliyunIEditor != null && !mAliyunIEditor.isPlaying()) {
            mAliyunIEditor.play();
            mAliyunIEditor.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAliyunIEditor != null && mAliyunIEditor.isPlaying()) {
            mAliyunIEditor.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAliyunIEditor != null) {
            mAliyunIEditor.onDestroy();
        }

    }

    private int getSurfaceHeight(int width) {

        float height = width / videoRatio;
        return (int)height;
    }
}