package com.aliyun.svideo.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.aliyun.svideo.common.R;

public class CustomProgressDialog extends Dialog {
    private Context context;
    private TextView mTitle, mMessage, mProgress;
    private ProgressBar mProgressBar;
    private String mStringTitle, mStringMessage;
    private int mCurrentProgress, mMaxProgress;

    public CustomProgressDialog(Context context, String title, String message) {
        super(context, R.style.CustomDialogStyle);
        this.context = context;
        this.mStringTitle = title;
        this.mStringMessage = message;
    }

    public CustomProgressDialog(Context context) {
        this(context, "", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.alivc_common_dialog_progress, null, false);
        mTitle = view.findViewById(R.id.tv_dialog_title);
        mMessage = view.findViewById(R.id.tv_dialog_message);
        mProgressBar = view.findViewById(R.id.pb_dialog_progress);
        mProgress = view.findViewById(R.id.tv_dialog_progress);
        if (mStringTitle != null) {
            mTitle.setText(mStringTitle);
        }
        if (mStringMessage != null) {
            mMessage.setText(mStringMessage);
        }
        if (mCurrentProgress != 0) {
            mProgressBar.setProgress(mCurrentProgress);
        }
        if (mMaxProgress != 0) {
            mProgressBar.setMax(mMaxProgress);
        } else {
            mProgressBar.setMax(100);
        }
        setProgress(mCurrentProgress);
        setContentView(view);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // ????????????????????????
        lp.width = d.widthPixels; // ???????????????????????????
        lp.height = d.heightPixels; // ???????????????????????????
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        dialogWindow.setAttributes(lp);
    }

    /**
     * ????????????
     * @param stringTitle string
     */
    public void setTitle(String stringTitle) {
        mStringTitle = stringTitle;
        if (mTitle != null) {
            mTitle.setText(mStringTitle);
        }
    }

    /**
     * ??????message
     * @param stringMessage string
     */
    public void setMessage(String stringMessage) {
        mStringMessage = stringMessage;
        if (mMessage != null) {
            mMessage.setText(mStringMessage);
        }
    }

    /**
     * ???????????????
     * @param progress int
     */
    public void setProgress(int progress) {
        mCurrentProgress = progress;
        if (mProgressBar != null) {
            mProgressBar.setProgress(progress);
            mProgress.setText(mCurrentProgress * 100 / mMaxProgress + "%");
        }
    }

    /**
     * ?????????????????????
     * @param progress int
     */
    public void setMaxProgress(int progress) {
        mMaxProgress = progress;
        if (mProgressBar != null) {
            mProgressBar.setMax(progress);
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
