package com.jagmee.app.alivcsolution.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.svideo.common.utils.ToastUtils;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;
import com.aliyun.svideo.sdk.external.struct.common.AliyunVideoParam;
import com.jagmee.app.Main_Menu.MainMenuActivity;
import com.jagmee.app.R;

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String KEY_PARAM_CONFIG = "project_json_path";
    public static final String KEY_PARAM_THUMBNAIL = "svideo_thumbnail";
    public static final String KEY_PARAM_DESCRIBE = "svideo_describe";
    public static final String KEY_PARAM_VIDEO_RATIO = "key_param_video_ratio";
    private static final String KEY_PARAM_VIDEO_PARAM = "videoParam";

    private ImageView mIvLeft;
    /**
     * 发布按钮
     */
    private TextView mTvPublish;
    /**
     * 显示封面
     */
    private ImageView mIvCover;
    private ImageView mWhatsapp;
    private ImageView mFb;
    private ImageView mInsta;
    private ImageView mShare;
    /**
     * 输入视频描述
     */
    private EditText mEtVideoDescribe;
    /**
     * 配置文件地址
     */
    private String mConfigPath;
    /**
     * 封面图片地址
     */
    private String mThumbnailPath;
    private float videoRatio;
    private AliyunVideoParam mVideoPram;

    /**
     * 视频描述文字最大输入长度
     */
    private static final int MAX_INPUT_DESC_LENGTH = 20;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_little_activity_publish);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        mConfigPath = intent.getStringExtra(KEY_PARAM_CONFIG);
        mThumbnailPath = intent.getStringExtra(KEY_PARAM_THUMBNAIL);
        videoRatio = intent.getFloatExtra(KEY_PARAM_VIDEO_RATIO, 0f);
        mVideoPram = (AliyunVideoParam)intent.getSerializableExtra(KEY_PARAM_VIDEO_PARAM);
        new ImageLoaderImpl().loadImage(this, mThumbnailPath,
                new ImageLoaderOptions.Builder().
                        skipMemoryCache().
                        skipDiskCacheCache().
                        build()).into(mIvCover);

    }

    private void initView() {
        mIvLeft = (ImageView)findViewById(R.id.iv_left);
        mIvLeft.setOnClickListener(this);
        mTvPublish = (TextView)findViewById(R.id.tv_publish);
        mTvPublish.setOnClickListener(this);
        mIvCover = (ImageView)findViewById(R.id.iv_cover);
        mWhatsapp = (ImageView)findViewById(R.id.img_wa);
        mFb = (ImageView)findViewById(R.id.img_fb);
        mInsta = (ImageView)findViewById(R.id.img_insta);
        mShare = (ImageView)findViewById(R.id.img_more);
        mWhatsapp.setOnClickListener(this);
        mFb.setOnClickListener(this);
        mInsta.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mIvCover.setOnClickListener(this);
        mEtVideoDescribe = (EditText)findViewById(R.id.et_video_describe);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_left) {
            onBackPressed();
        } else if (i == R.id.tv_publish) {
            String describe = mEtVideoDescribe.getText().toString();
            if (describe.length() > MAX_INPUT_DESC_LENGTH) {
                ToastUtils.show(this, getResources().getString(R.string.alivc_little_publish_tip_description_beyond));
                return;
            }

            Intent intent = new Intent(PublishActivity.this, MainMenuActivity.class);
            intent.putExtra(KEY_PARAM_THUMBNAIL, mThumbnailPath);
            intent.putExtra(KEY_PARAM_CONFIG, mConfigPath);

            intent.putExtra(KEY_PARAM_DESCRIBE, describe);
            startActivity(intent);

        } else if (i == R.id.iv_cover) {
            startPreview();
        } else if (i == R.id.img_wa) {
            shareOnWhatapp();
        } else if (i == R.id.img_fb) {
            shareOnFb();
        } else if (i == R.id.img_insta) {
            shareOnInsta();
        } else if (i == R.id.img_more) {
            share();
        }
    }



    public void shareVideo(String pkgname) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setPackage(pkgname);
        share.putExtra(Intent.EXTRA_STREAM, mUri);
        share.setType("Video/*");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, "Vande"));
    }

    private void share() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, mUri);
        share.setType("Video/*");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, "Vande"));
    }

    private void shareOnInsta() {
        if(isPackageInstalled("com.instagram.android")) {
            shareVideo("com.instagram.android");
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Warning");
            alert.setMessage("Instagram App not found");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
        }
    }

    private void shareOnFb() {
        if(isPackageInstalled("com.facebook.katana")) {
            shareVideo("com.facebook.katana");
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Warning");
            alert.setMessage("Facebook App not found");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
        }
    }

    private void shareOnWhatapp() {
        if(isPackageInstalled("com.whatsapp")) {
            shareVideo("com.whatsapp");
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Warning");
            alert.setMessage("Whatsapp App not found");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
        }
    }

    private boolean isPackageInstalled(String packagename) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void startPreview() {
        Intent intent = new Intent(this, AlivcLittlePreviewActivity.class);
        intent.putExtra(KEY_PARAM_CONFIG, mConfigPath);
        intent.putExtra(KEY_PARAM_VIDEO_PARAM, mVideoPram);
        //传入视频比列
        intent.putExtra(KEY_PARAM_VIDEO_RATIO, videoRatio);
        startActivity(intent);
    }

}