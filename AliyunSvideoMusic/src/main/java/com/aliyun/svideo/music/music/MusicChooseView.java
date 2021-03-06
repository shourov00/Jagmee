package com.aliyun.svideo.music.music;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aliyun.svideo.base.Constants;
import com.aliyun.svideo.common.utils.MD5Utils;
import com.aliyun.svideo.common.utils.UriUtils;
import com.aliyun.svideo.music.R;
import com.aliyun.common.utils.ToastUtil;
import com.aliyun.svideo.downloader.FileDownloaderCallback;
import com.aliyun.svideo.base.CopyrightWebActivity;
import com.aliyun.svideo.base.http.MusicFileBean;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class MusicChooseView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "MusicChooseView";
    private ImageView mAliyunBackBtn;
    private TextView mAliyunCompeletBtn;
    private TextView mAliyunOnlineMusic;
    private TextView mAliyunLocalMusic;
    private TextView mAliyunLocalMusicVideo;
    private RecyclerView mAliyunMusicRecyclerView;
    private MusicAdapter mMusicAdapter;
    private Handler mPlayHandler = new Handler(Looper.getMainLooper());
    private MediaPlayer mMediaPlayer;
    //??????????????????
    private int mRecordTime = 10 * 1000;
    //???????????????????????????
    private int mStartTime = 0;
    //????????????????????????
    private int mLoopTime = 10 * 1000;
    //?????????????????????????????????
    private boolean isLocalMusic = false;
    private boolean isLocalMusicVideo = false;
    private int playedTime;
    private ArrayList<EffectBody<MusicFileBean>> mLocalMusicList = new ArrayList<>();
    private ArrayList<EffectBody<MusicFileBean>> mLocalMusicVideoList = new ArrayList<>();
    private ArrayList<EffectBody<MusicFileBean>> mOnlineMusicList = new ArrayList<>();
    private MediaMetadataRetriever mmr;
    private MusicLoader musicLoader;
    private MusicSelectListener musicSelectListener;
    private MusicFileBean mSelectMusic;
    private TextView mAlivcMusicCopyrightTV;
    /**
     * ???????????????
     */
    private int mSelectPosition;

    private boolean isViewAttached;
    /**
     * ????????????????????????????????????, ???????????????, ?????????????????????????????????
     */
    private boolean isVisible;

    /**
     * ??????????????????????????????
     */
    boolean isShowed;
    /**
     * ???????????????????????????
     */
    private MusicFileBean mCacheMusic;
    /**
     * ???????????????????????????
     */
    private int mCacheStartTime;
    /**
     * ???????????????????????????
     */
    private int mCachePosition;
    /**
     * ?????????????????????tab ??????/??????
     */
    private boolean mCacheIsLocalMusic;
    private boolean mCacheIsLocalMusicVideo;

    public MusicChooseView(Context context) {
        super(context);
        init();
    }

    public MusicChooseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MusicChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
        initData();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mMediaPlayer = new MediaPlayer();
        mmr = new MediaMetadataRetriever();
        isViewAttached = true;
        setVisibleStatus(true);
        if (mCacheIsLocalMusic) {
            selectLocalTab(mCachePosition);
        } else if (mCacheIsLocalMusicVideo) {
            selectLocalTabVideo(mCachePosition);
        } else {
            selectOnlineTab(mCachePosition);
        }
        //?????????????????????????????????????????? ??????????????????
        if (isShowed && mCacheMusic != null && mMusicAdapter != null) {

            mMusicAdapter.notifySelectPosition(mCacheStartTime, mCachePosition);
            mAliyunMusicRecyclerView.scrollToPosition(mCachePosition);
            Log.d(TAG, "onAttachedToWindow notifySelectPosition");
            try {
                prepareMusicInfo(mCacheMusic);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setLooping(true);
            mPlayHandler.postDelayed(mMusciRunnable, 0);
        } else if (isShowed && mMusicAdapter != null) {
            mMusicAdapter.notifySelectPosition(0, 0);
            mAliyunMusicRecyclerView.scrollToPosition(0);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setVisibleStatus(false);
        isViewAttached = false;
        mPlayHandler.removeCallbacks(mMusciRunnable);
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mmr.release();
    }


    private void initData() {
        if (musicLoader == null) {
            musicLoader = new MusicLoader(getContext());
            musicLoader.setCallback(new MusicLoader.LoadCallback() {
                @Override
                public void onLoadLocalMusicVideoCompleted(List<EffectBody<MusicFileBean>> loacalMusicVideo) {
                    mLocalMusicVideoList.clear();
                    mLocalMusicVideoList.addAll(loacalMusicVideo);
                    if (isLocalMusicVideo) {
                        mMusicAdapter.setData(mLocalMusicVideoList, 0);
                    }
                }

                @Override
                public void onLoadLocalMusicCompleted(List<EffectBody<MusicFileBean>> loacalMusic) {
                    mLocalMusicList.clear();
                    mLocalMusicList.addAll(loacalMusic);
                    if (isLocalMusic) {
                        mMusicAdapter.setData(mLocalMusicList, 0);
                    }
                }

                @Override
                public void onLoadNetMusicCompleted(List<EffectBody<MusicFileBean>> netMusic) {
                    mOnlineMusicList.addAll(netMusic);
                    if (!isLocalMusic) {
                        mMusicAdapter.setData(mOnlineMusicList, 0);
                    }
                }

                @Override
                public void onSearchNetMusicCompleted(List<EffectBody<MusicFileBean>> result) {

                }
            });
            musicLoader.loadAllMusic();
        }

    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_music_view_chooser_music, this, true);
        mAliyunBackBtn = findViewById(R.id.aliyun_back_btn);
        mAliyunBackBtn.setOnClickListener(this);
        mAliyunCompeletBtn = findViewById(R.id.aliyun_compelet_btn);
        mAliyunCompeletBtn.setOnClickListener(this);
        mAliyunOnlineMusic = findViewById(R.id.aliyun_online_music);
        mAliyunOnlineMusic.setOnClickListener(this);
        mAliyunLocalMusic = findViewById(R.id.aliyun_local_music);
        mAliyunLocalMusic.setOnClickListener(this);
        mAliyunLocalMusicVideo = findViewById(R.id.aliyun_local_music_video);
        mAliyunLocalMusicVideo.setOnClickListener(this);
        mAliyunMusicRecyclerView = findViewById(R.id.aliyun_music_list);
        mAlivcMusicCopyrightTV = findViewById(R.id.alivc_music_copyright);
       // mAlivcMusicCopyrightTV.setText(getClickalbeSpan());
      //  mAlivcMusicCopyrightTV.setMovementMethod(LinkMovementMethod.getInstance());
        mAlivcMusicCopyrightTV.setVisibility(GONE);
        setFocusable(true);

        mAliyunMusicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (mMusicAdapter == null) {
            mMusicAdapter = new MusicAdapter();
            mMusicAdapter.setStreamDuration(mRecordTime);
            mMusicAdapter.setOnMusicSeekListener(new MusicAdapter.OnMusicSeek() {
                @Override
                public void onSeekStop(long start) {
                    mPlayHandler.removeCallbacks(mMusciRunnable);
                    mStartTime = (int)start;
                    mPlayHandler.postDelayed(mMusciRunnable, 0);
                }

                @Override
                public void onSelectMusic(final int position, final EffectBody<MusicFileBean> effectBody) {
                    final MusicFileBean musicFileBean = effectBody.getData();
                    mSelectMusic = musicFileBean;
                    mSelectPosition = position;

                    //????????????????????????????????????
                    if (effectBody.isLocal()) {
                        onMusicSelected(musicFileBean, position);
                    } else {

                        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                            //???????????????????????????????????????
                            mMediaPlayer.stop();
                        }
                        musicLoader.downloadMusic(musicFileBean, new FileDownloaderCallback() {
                            @Override
                            public void onStart(int downloadId, long soFarBytes, long totalBytes, int preProgress) {
                                super.onStart(downloadId, soFarBytes, totalBytes, preProgress);
                                if (!isLocalMusic) {
                                    mMusicAdapter.notifyDownloadingStart(effectBody);
                                }

                            }

                            @Override
                            public void onProgress(int downloadId, long soFarBytes, long totalBytes, long speed,
                                                   int progress) {
                                super.onProgress(downloadId, soFarBytes, totalBytes, speed, progress);
                                if (!isLocalMusic) {
                                    mMusicAdapter.updateProcess(
                                        (MusicAdapter.MusicViewHolder) mAliyunMusicRecyclerView
                                        .findViewHolderForAdapterPosition(position), progress, position);
                                }
                            }

                            @Override
                            public void onFinish(int downloadId, String path) {
                                super.onFinish(downloadId, path);
                                effectBody.getData().setPath(path);
                                if (mMusicAdapter == null) {
                                    return;
                                }
                                // ??????????????????, ???????????????????????????
                                // ?????????????????????????????????, ???????????????????????????????????????, ?????????????????????99%?????????
                                if (position == mMusicAdapter.getSelectIndex() && !isLocalMusic) {
                                    onMusicSelected(effectBody.getData(), position);

                                }
                                mMusicAdapter.notifyDownloadingComplete((MusicAdapter.MusicViewHolder)
                                                                        mAliyunMusicRecyclerView
                                                                        .findViewHolderForAdapterPosition(position), effectBody, position);

                            }

                            @Override
                            public void onError(BaseDownloadTask task, Throwable e) {
                                super.onError(task, e);
                                ToastUtil.showToast(getContext(), e.getMessage());
                            }
                        });
                    }
                }
            });
        }
        mAliyunMusicRecyclerView.setAdapter(mMusicAdapter);
        mAliyunMusicRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !isLocalMusic && !isLocalMusicVideo) {
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int totalItemCount = recyclerView.getAdapter().getItemCount();
                    int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                    int visibleItemCount = recyclerView.getChildCount();

                    if (lastVisibleItemPosition > totalItemCount - 5
                            && visibleItemCount > 5) {
                        //????????????
                        musicLoader.loadMoreOnlineMusic();
                    }
                }
            }
        });


    }

    private void onMusicSelected(MusicFileBean musicFileBean, int position) {

        if (mCachePosition != position ) {
            //????????????????????????
            mStartTime = 0;
        } else {
            mStartTime = mCacheStartTime;
        }

        if (mCacheIsLocalMusic != isLocalMusic) {
            //??????tab?????????startTime
            mStartTime = 0;
        }
        try {
            if (isVisible) {
                prepareMusicInfo(musicFileBean);
                mMusicAdapter.notifyItemChanged(position);
                mMediaPlayer.setLooping(true);
                mPlayHandler.postDelayed(mMusciRunnable, 0);
            } else if (isShowed) {
                // ?????????????????????, ??????????????????, ????????????item??????, ???????????????
                prepareMusicInfo(musicFileBean);
                mMusicAdapter.notifyItemChanged(position);
                mMediaPlayer.setLooping(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????????????????
     */
    private void prepareMusicInfo(MusicFileBean musicFileBean) throws IOException, IllegalStateException {
        mPlayHandler.removeCallbacks(mMusciRunnable);
        mMediaPlayer.reset();
        Uri uri = null;
        if (!TextUtils.isEmpty(musicFileBean.path)) {
            uri = Uri.parse(musicFileBean.path);
        }
        if (!TextUtils.isEmpty(musicFileBean.uri)) {
            uri = Uri.parse(musicFileBean.uri);
        }
        if (uri == null) {
            return;
        }
        mMediaPlayer.setDataSource(getContext(), uri);
        mMediaPlayer.prepare();

        int duration = mMediaPlayer.getDuration();
        if (mSelectMusic != null) {
            mSelectMusic.duration = duration;
        }
        if (duration < mRecordTime) {
            mLoopTime = duration;
        } else {
            mLoopTime = mRecordTime;
        }
        musicFileBean.setDuration(duration);

    }

    private Runnable mMusciRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (MusicChooseView.class) {
                if (isVisible) {

                    mMediaPlayer.seekTo(mStartTime);
                    mMediaPlayer.start();
                    mPlayHandler.postDelayed(this, mLoopTime);
                }
            }

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //???????????????????????????????????????mAliyunCompeletBtn??????
            mSelectMusic = mCacheMusic;
            mStartTime = mCacheStartTime;
            mSelectPosition = mCachePosition;
            isLocalMusic = mCacheIsLocalMusic;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v == mAliyunBackBtn) {
            if (musicSelectListener != null) {
                musicSelectListener.onCancel();
                //???????????????????????????????????????mAliyunCompeletBtn????????????????????????????????????
                mSelectMusic = mCacheMusic;
                mStartTime = mCacheStartTime;
                mSelectPosition = mCachePosition;
                isLocalMusic = mCacheIsLocalMusic;
            }
        } else if (v == mAliyunCompeletBtn) {

            if (musicSelectListener != null) {
                Log.i(TAG, "log_music_start_time : " + mStartTime);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !TextUtils.isEmpty(mSelectMusic.uri)) {
                    //??????Android Q???????????????copy?????????????????????
                    String filePath = Constants.SDCardConstants.getCacheDir(getContext()) + File.separator + MD5Utils
                                      .getMD5(mSelectMusic.uri) + ".mp3";

                    if (new File(filePath).exists() || UriUtils.copyFileToDir(getContext(), mSelectMusic.uri, filePath)) {
                        //??????????????????copy???????????????path
                        mSelectMusic.path = filePath;
                    }
                }
                musicSelectListener.onMusicSelect(mSelectMusic, mStartTime);
                //??????????????????
                mCacheMusic = mSelectMusic;
                mCacheStartTime = mStartTime;
                mCachePosition = mSelectPosition;
                mCacheIsLocalMusic = isLocalMusic;
            }
        } else if (v == mAliyunOnlineMusic) {
            Log.e("TAB OnlineMusic", "Working");
            if (isLocalMusic) {
                isLocalMusic = false;
                selectOnlineTab(0);
            } else if (isLocalMusicVideo) {
                isLocalMusicVideo = false;
                selectOnlineTab(0);
            }
        } else if (v == mAliyunLocalMusic) {
            Log.e("TAB LocalMusic", "Working");
            if (!isLocalMusic) {
                isLocalMusic = true;
                isLocalMusicVideo = false;
                selectLocalTab(0);
            }
        } else if (v == mAliyunLocalMusicVideo) {
            Log.e("TAB LocalMusicVideo", "Working");
            if (!isLocalMusicVideo) {
                Log.e("TAB LocalMusicVideo", "Working");
                isLocalMusicVideo = true;
                isLocalMusic = false;
                selectLocalTabVideo(0);
            }
        }
    }

    private void selectOnlineTab(int index) {
        mMusicAdapter.setData(mOnlineMusicList, index);
        mAlivcMusicCopyrightTV.setVisibility(View.GONE);
        mAliyunOnlineMusic.setSelected(true);
        mAliyunLocalMusic.setSelected(false);
        mAliyunLocalMusicVideo.setSelected(false);
    }

    private void selectLocalTab(int index) {
        mMusicAdapter.setData(mLocalMusicList, index);
        mAlivcMusicCopyrightTV.setVisibility(View.INVISIBLE);
        mAliyunOnlineMusic.setSelected(false);
        mAliyunLocalMusic.setSelected(true);
        mAliyunLocalMusicVideo.setSelected(false);
    }

    private void selectLocalTabVideo(int index) {
        mMusicAdapter.setData(mLocalMusicVideoList, index);
        mAlivcMusicCopyrightTV.setVisibility(View.INVISIBLE);
        mAliyunOnlineMusic.setSelected(false);
        mAliyunLocalMusic.setSelected(false);
        mAliyunLocalMusicVideo.setSelected(true);
    }

    public void setMusicSelectListener(MusicSelectListener musicSelectListener) {
        this.musicSelectListener = musicSelectListener;
    }

    public void setStreamDuration(int streamTime) {
        this.mRecordTime = streamTime;
        if (mMusicAdapter != null) {
            mMusicAdapter.setStreamDuration(streamTime);
        }
    }

    Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (MusicChooseView.class) {
                if (isVisible) {
                    mMediaPlayer.start();
                }
            }
        }
    };

    /**
     * ??????view???????????????, ???????????????, ?????????????????????, ????????????,????????????
     *
     * @param visibleStatus true: ??????, false: ?????????
     */
    public void setVisibleStatus(final boolean visibleStatus) {
        if (isViewAttached) {
            if (visibleStatus) {
                //?????????view????????????????????????????????????????????????????????????????????????
                if (mOnlineMusicList.isEmpty()) {
                    musicLoader.loadMoreOnlineMusic();
                }
                //???????????????????????????????????????onResume???onPause?????????????????????
                mPlayHandler.removeCallbacks(mStartRunnable);
                mPlayHandler.removeCallbacks(mMusciRunnable);
                mPlayHandler.postDelayed(mStartRunnable, 500);
                mPlayHandler.postDelayed(mMusciRunnable, mLoopTime - playedTime);
                isShowed = true;

            } else {
                mPlayHandler.removeCallbacks(mStartRunnable);
                mPlayHandler.removeCallbacks(mMusciRunnable);
                if (mMediaPlayer.isPlaying()) {
                    playedTime = mMediaPlayer.getCurrentPosition() - mStartTime;
                    synchronized (MusicChooseView.class) {
                        mMediaPlayer.pause();
                        isVisible = false;
                    }
                }
            }
        }
        isVisible = visibleStatus;
    }

    /**
     * ???????????????????????????????????????
     * @return
     */
    private SpannableString getClickalbeSpan() {
        String copyright = getContext().getResources().getString(R.string.alivc_music_copyright);
        String copyrightLink = getContext().getResources().getString(R.string.alivc_music_copyright_link);
        final int start = copyright.length();
        int end = copyright.length() + copyrightLink.length();
        SpannableString spannableString = new SpannableString(copyright + copyrightLink);
        spannableString.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(getContext(), CopyrightWebActivity.class);
                getContext().startActivity(intent);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.alivc_common_cyan_light)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
