package com.jagmee.app.Home;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.aliyun.svideo.recorder.activity.AlivcSvideoMixRecordActivity;
import com.aliyun.svideo.recorder.bean.RenderingMode;
import com.aliyun.svideo.sdk.external.struct.common.VideoDisplayMode;
import com.aliyun.svideo.sdk.external.struct.common.VideoQuality;
import com.aliyun.svideo.sdk.external.struct.encoder.VideoCodecs;
import com.aliyun.svideo.sdk.external.struct.snap.AliyunSnapVideoParam;
import com.daasuu.mp4compose.FillMode;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.daasuu.mp4compose.filter.GlFilterGroup;
import com.daasuu.mp4compose.filter.GlMonochromeFilter;
import com.daasuu.mp4compose.filter.GlVignetteFilter;
import com.daasuu.mp4compose.filter.GlWatermarkFilter;
import com.danikula.videocache.HttpProxyCacheServer;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.jagmee.app.Accounts.Login_A;
import com.jagmee.app.Services.Upload_Service;
import com.jagmee.app.SimpleClasses.ApiRequest;
import com.jagmee.app.SimpleClasses.Callback;
import com.jagmee.app.SimpleClasses.TicTic;
import com.jagmee.app.SoundLists.VideoSound_A;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jagmee.app.Comments.Comment_F;
import com.jagmee.app.Main_Menu.MainMenuActivity;
import com.jagmee.app.Main_Menu.MainMenuFragment;
import com.jagmee.app.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.jagmee.app.Profile.Profile_F;
import com.jagmee.app.R;
import com.jagmee.app.SimpleClasses.API_CallBack;
import com.jagmee.app.SimpleClasses.Fragment_Callback;
import com.jagmee.app.SimpleClasses.Fragment_Data_Send;
import com.jagmee.app.SimpleClasses.Functions;
import com.jagmee.app.SimpleClasses.Variables;
import com.jagmee.app.Taged.Taged_Videos_F;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.jagmee.app.WatchVideos.BottomSheetFragment;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.aliyun.svideo.common.utils.ThreadUtils.runOnUiThread;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */

// this is the main view which is show all  the video in list
public class Home_F extends RootFragment implements Player.EventListener, Fragment_Data_Send,View.OnClickListener {
    private static final String TAG = "Home_F";
    //musical
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    private long lastFavClick=-1;
    View view;
    Context context;
    public String action;

    RecyclerView recyclerView;
    ArrayList<Home_Get_Set> data_list;
    int currentPage=-1;
    LinearLayoutManager layoutManager;
    ProgressBar p_bar;
    SwipeRefreshLayout swiperefresh;
    boolean is_user_stop_video=false;
    Dialog dialog;

    TextView following_btn,related_btn;
    String type="related";

    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    private ThinDownloadManager downloadManager;

    public Home_F() {
        // Required empty public constructor
    }

    int swipe_count=0;


    RelativeLayout upload_video_layout;
    ImageView uploading_thumb;
    ImageView uploading_icon;
    UploadingVideoBroadCast mReceiver;

    private class UploadingVideoBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Upload_Service mService = new Upload_Service();
            if (Functions.isMyServiceRunning(context,mService.getClass())) {
                upload_video_layout.setVisibility(View.VISIBLE);
                Bitmap bitmap=Functions.Base64_to_bitmap(Variables.sharedPreferences.getString(Variables.uploading_video_thumb,""));
                if(bitmap!=null)
                uploading_thumb.setImageBitmap(bitmap);

            }
            else {
                upload_video_layout.setVisibility(View.GONE);
            }

        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home, container, false);
        context=getContext();
        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);

        //musical
        permissionStatus = context.getSharedPreferences("permissionStatus",MODE_PRIVATE);

        p_bar=view.findViewById(R.id.p_bar);

        following_btn=view.findViewById(R.id.following_btn);
        related_btn=view.findViewById(R.id.related_btn);

        following_btn.setOnClickListener(this);
        related_btn.setOnClickListener(this);


        recyclerView=view.findViewById(R.id.recylerview);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);

         SnapHelper snapHelper =  new PagerSnapHelper();
         snapHelper.attachToRecyclerView(recyclerView);



        // this is the scroll listener of recycler view which will tell the current item number
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //here we find the current item number
                final int scrollOffset = recyclerView.computeVerticalScrollOffset();
                final int height = recyclerView.getHeight();
                int page_no=scrollOffset / height;

                if(page_no!=currentPage ){
                    currentPage=page_no;

                    Release_Privious_Player();
                    Set_Player(currentPage);

                }
            }
        });



        swiperefresh=view.findViewById(R.id.swiperefresh);
        swiperefresh.setProgressViewOffset(false, 0, 200);

        swiperefresh.setColorSchemeResources(R.color.black);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Call_Api_For_get_Allvideos();
            }
        });

        Call_Api_For_get_Allvideos();

        if(!Variables.is_remove_ads)
        Load_add();

        upload_video_layout=view.findViewById(R.id.upload_video_layout);
        uploading_thumb=view.findViewById(R.id.uploading_thumb);
        uploading_icon=view.findViewById(R.id.uploading_icon);

        mReceiver = new UploadingVideoBroadCast();
        getActivity().registerReceiver(mReceiver, new IntentFilter("uploadVideo"));

        Upload_Service mService = new Upload_Service();
        if (Functions.isMyServiceRunning(context,mService.getClass())) {
            upload_video_layout.setVisibility(View.VISIBLE);
            Bitmap bitmap=Functions.Base64_to_bitmap(Variables.sharedPreferences.getString(Variables.uploading_video_thumb,""));
            if(bitmap!=null)
            uploading_thumb.setImageBitmap(bitmap);
        }


        return view;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.following_btn:

                if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
                    type = "following";
                    swiperefresh.setRefreshing(true);
                    related_btn.setTextColor(context.getResources().getColor(R.color.graycolor2));
                    following_btn.setTextColor(context.getResources().getColor(R.color.white));
                    Call_Api_For_get_Allvideos();
                }
                else {
                    Open_Login();
                }
                break;

            case R.id.related_btn:
                type="related";
                swiperefresh.setRefreshing(true);
                related_btn.setTextColor(context.getResources().getColor(R.color.white));
                following_btn.setTextColor(context.getResources().getColor(R.color.graycolor2));
                Call_Api_For_get_Allvideos();
                break;
        }

    }


    InterstitialAd mInterstitialAd;
    public void Load_add() {

        // this is test app id you will get the actual id when you add app in your
        //add mob account
        MobileAds.initialize(context, getResources().getString(R.string.ad_app_id));


        //code for intertial add
        mInterstitialAd = new InterstitialAd(context);

        //here we will get the add id keep in mind above id is app id and below Id is add Id
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.my_Interstitial_Add));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });


    }






    boolean is_add_show=false;
    Home_Adapter adapter;
    public void Set_Adapter(){

         adapter=new Home_Adapter(context, data_list, new Home_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, final Home_Get_Set item, View view) {

                switch(view.getId()) {

                   case R.id.user_pic:
                        onPause();
                        OpenProfile(item,false);
                        break;

                    case R.id.username:
                        onPause();
                        OpenProfile(item,false);
                        break;

                    case R.id.like_layout:
                        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {

                        Like_Video(postion, item);
                        }else {
                            Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.comment_layout:
                        OpenComment(item);
                        break;

                    case R.id.shared_layout:
                        /*if (!is_add_show && (mInterstitialAd!=null && mInterstitialAd.isLoaded())) {
                            mInterstitialAd.show();
                            is_add_show = true;
                        } else {
                             is_add_show = false;*/

//                        if (!is_add_show &&  (interstitialAd!=null && interstitialAd.isAdLoaded())) {
//                            Log.e(TAG, "onItemClick: fb ad loaded" );
//                            interstitialAd.show();
//                            is_add_show = true;
//                        } else {
//                            Log.e(TAG, "onItemClick: ad not loaded" );
//                            is_add_show = false;

                            //imei
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                        //Show Information about why you need the permission
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle("Need Storage Permission");
                                        builder.setMessage("This app needs storage permission.");
                                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        builder.show();
                                    } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
                                        //Previously Permission Request was cancelled with 'Dont Ask Again',
                                        // Redirect to Settings after showing Information about why you need the permission
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle("Need Storage Permission");
                                        builder.setMessage("This app needs storage permission.");
                                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                sentToSettings = true;
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                                intent.setData(uri);
                                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                                Toast.makeText(context, "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        builder.show();
                                    } else {
                                        //just request the permission
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                                    }

                                    SharedPreferences.Editor editor = permissionStatus.edit();
                                    editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
                                    editor.commit();


                                } else {
                                    //You already have the permission, just go ahead.
                                    TicTic.shareurl = item.video_url;
                                    TicTic.downloadurl = item.video_url;
                                    TicTic.videoId = item.video_id;
                                    TicTic.videoUsername = item.username;

                                    BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                                    bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
                                }
                            }
                            else{
                                TicTic.shareurl = item.video_url;
                                TicTic.downloadurl = item.video_url;
                                TicTic.videoId = item.video_id;
                                TicTic.videoUsername = item.username;

                                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                                bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
                            }


//                                final VideoAction_F fragment = new VideoAction_F(item.video_id, new Fragment_Callback() {
//                                    @Override
//                                    public void Responce(Bundle bundle) {
//
//                                        if (bundle.getString("action").equals("save")) {
//                                            Save_Video(item);
//                                        } else if (bundle.getString("action").equals("delete")) {
//                                            Functions.Show_loader(context, false, false);
//                                            Functions.Call_Api_For_Delete_Video(getActivity(), item.video_id, new API_CallBack() {
//                                                    @Override
//                                                    public void ArrayData(ArrayList arrayList) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void OnSuccess(String responce) {
//                                                        data_list.remove(currentPage);
//                                                        adapter.notifyDataSetChanged();
//                                                    }
//
//                                                    @Override
//                                                    public void OnFail(String responce) {
//
//                                                    }
//                                                });
//
//                                        }
//
//                                    }
//                                });
//
//                                Bundle bundle = new Bundle();
//                                bundle.putString("video_id", item.video_id);
//                                bundle.putString("user_id", item.fb_id);
//                                fragment.setArguments(bundle);
//                                fragment.show(getChildFragmentManager(), "");
//                            }

                        break;

                    case R.id.download_layout:
                        action = "download";

                        /*if (!is_add_show &&  (mInterstitialAd!=null && mInterstitialAd.isLoaded())) {
                            Log.e(TAG, "onItemClick: admob ad loaded" );
                            mInterstitialAd.show();
                            is_add_show = true;
                        } else {
                            Log.e(TAG, "onItemClick: admob not loaded");
                            is_add_show = false;
                        }

                        if(Functions.Checkstoragepermision(getActivity()))

                            Save_Video(item, false);*/

                        //new code start
                        /*if (!is_add_show && (mInterstitialAd!=null && mInterstitialAd.isLoaded())) {
                            mInterstitialAd.show();
                            is_add_show = false;
                        } else {
                            is_add_show = false;*/

//                        if (!is_add_show &&  (interstitialAd!=null && interstitialAd.isAdLoaded())) {
//                            Log.e(TAG, "onItemClick: fb ad loaded" );
//                            interstitialAd.show();
//                            is_add_show = true;
//                        } else {
//                            Log.e(TAG, "onItemClick: ad not loaded" );
//                            is_add_show = false;

                            //imei
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                        //Show Information about why you need the permission
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle("Need Storage Permission");
                                        builder.setMessage("This app needs storage permission.");
                                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        builder.show();
                                    } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
                                        //Previously Permission Request was cancelled with 'Dont Ask Again',
                                        // Redirect to Settings after showing Information about why you need the permission
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle("Need Storage Permission");
                                        builder.setMessage("This app needs storage permission.");
                                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                sentToSettings = true;
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                                intent.setData(uri);
                                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                                Toast.makeText(context, "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        builder.show();
                                    } else {
                                        //just request the permission
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                                    }

                                    SharedPreferences.Editor editor = permissionStatus.edit();
                                    editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
                                    editor.commit();


                                } else {
                                    Log.e("TAG download", "Came inside inner else");
                                    //You already have the permission, just go ahead.
                                    TicTic.shareurl = item.video_url;
                                    TicTic.downloadurl = item.video_url;
                                    TicTic.videoId = item.video_id;
                                    TicTic.videoUsername = item.username;
                                    try {
                                        //fbdownloadvideo(TicTic.downloadurl, TicTic.videoId);
                                        fbdownloadvideo(TicTic.downloadurl);
                                    } catch (Exception ed) {
                                        Toast.makeText(context, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                                        //circle_progress.setVisibility(View.GONE);
                                    }
                                    /*BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                                    bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());*/
                                }
                            }
                            else{
                                Log.e("TAG download", "Came inside main else");
                                TicTic.shareurl = item.video_url;
                                TicTic.downloadurl = item.video_url;
                                TicTic.videoId = item.video_id;
                                TicTic.videoUsername = item.username;

                                try {
                                    fbdownloadvideo(TicTic.downloadurl);
                                } catch (Exception ed) {
                                    Toast.makeText(context, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                                    //circle_progress.setVisibility(View.GONE);
                                }
                                /*BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                                bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());*/
                            }


//                                final VideoAction_F fragment = new VideoAction_F(item.video_id, new Fragment_Callback() {
//                                    @Override
//                                    public void Responce(Bundle bundle) {
//
//                                        if (bundle.getString("action").equals("save")) {
//                                            Save_Video(item);
//                                        } else if (bundle.getString("action").equals("delete")) {
//                                            Functions.Show_loader(context, false, false);
//                                            Functions.Call_Api_For_Delete_Video(getActivity(), item.video_id, new API_CallBack() {
//                                                    @Override
//                                                    public void ArrayData(ArrayList arrayList) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void OnSuccess(String responce) {
//                                                        data_list.remove(currentPage);
//                                                        adapter.notifyDataSetChanged();
//                                                    }
//
//                                                    @Override
//                                                    public void OnFail(String responce) {
//
//                                                    }
//                                                });
//
//                                        }
//
//                                    }
//                                });
//
//                                Bundle bundle = new Bundle();
//                                bundle.putString("video_id", item.video_id);
//                                bundle.putString("user_id", item.fb_id);
//                                fragment.setArguments(bundle);
//                                fragment.show(getChildFragmentManager(), "");
//                        }
                        //new code end
                        break;


                    case R.id.dute_layout:
                        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
                            Save_Video(item, true);
                        }else {
                            Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case R.id.sound_image_layout:
                        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
                            if(check_permissions()) {
                                Intent intent = new Intent(getActivity(), VideoSound_A.class);
                                intent.putExtra("data", item);
                                startActivity(intent);
                            }
                        }else {
                            Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                        }

                        break;


                    case R.id.report:
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            ReportPopup(postion, item);
                        } else {
                            Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                        }
                        break;



                    case R.id.myfollow:
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            if (item.follow.equals("0")) {
                                Follow_unFollow_User(postion, item, item.fb_id);
                            } else {
                                Follow_unFollow_User(postion, item, item.fb_id);
                            }
                        } else {
                            Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

            }
        });

        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

    }

    private void fbdownloadvideo(final String avideoUrl) {
        Log.e("TAG fbdownloadvideo", "Came  inside start");
        downloadvideo(avideoUrl);
        Log.e("TAG fbdownloadvideo", "Came  inside end");
        //Save_Video(videoUrl);
    }

    private void downloadvideo(String videoUrl) {
        System.out.println("Rajan_videoUrl" + videoUrl);
        //circle_progress.setVisibility(View.VISIBLE);
        //pleasewait.setVisibility(View.VISIBLE);
        Log.e("TAG fbdownloadvideo", "Came  inside start");

        Uri downloadUri = Uri.parse(videoUrl);
        final File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Jagmee");
        if (!file.exists()) {
            file.mkdirs();
        }
//        Random r = new Random();
//        final int i1 = r.nextInt();

        final Uri destinationUri = Uri.parse(file + File.separator + TicTic.videoId + "no_watermark" + ".mp4");
        com.thin.downloadmanager.DownloadRequest downloadRequest = new com.thin.downloadmanager.DownloadRequest(downloadUri)
                .addCustomHeader("Auth-Token", "YourTokenApiKey")
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(com.thin.downloadmanager.DownloadRequest.Priority.HIGH)
                .setDownloadContext("downloader")//Optional
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(com.thin.downloadmanager.DownloadRequest downloadRequest) {
                        Toast.makeText(context, "Video Downloaded", Toast.LENGTH_LONG).show();
                        /*circle_progress.setVisibility(View.GONE);
                        circle_progress.setProgress((float) 0);
                        circle_progress.setText(String.valueOf(0) + "%");*/
                        //pleasewait.setVisibility(View.GONE);

                        //scan file
                        Log.e(TAG, "onDownloadComplete: destinationUri " + destinationUri);
                        File yourFile = new File(String.valueOf(destinationUri));


                        try {
                            Log.e(TAG, "onDownloadComplete: file path " + yourFile.getAbsolutePath() + " video Id " + TicTic.videoId);
                            //  scanFile(yourFile.getAbsolutePath());
                        } catch (Exception e) {
//                            System.out.println("filescan"+e);
                        }


                        if (Build.VERSION.SDK_INT <= 21) {
                            if (action.contains("whatsapp")) {
                                //sendtowhatsapp(yourFile.getAbsolutePath());
                            } else if (action.contains("facebook")) {
                                //sendtofacebook(yourFile.getAbsolutePath());
                            } else if (action.contains("other")) {
                                //sendtoother(yourFile.getAbsolutePath());
                            } else if (action.contains("instagram")) {
                                //sendtoinstagram(yourFile.getAbsolutePath());
                            } else if (action.contains("twitter")) {
                                //sendtotwitter(yourFile.getAbsolutePath());
                            }
                        } else {
                            Applywatermark(TicTic.videoId);
                        }


                    }

                    @Override
                    public void onDownloadFailed(com.thin.downloadmanager.DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        Toast.makeText(context, "Download Failed", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onDownloadFailed: " + errorMessage);
                        /*circle_progress.setVisibility(View.GONE);
                        circle_progress.setProgress((float) 0);
                        circle_progress.setText(String.valueOf(0) + "%");*/
                        //pleasewait.setVisibility(View.GONE);
                    }

                    @Override
                    public void onProgress(com.thin.downloadmanager.DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
//                        System.out.println("Meera_onProgress"+ progress);
                        /*circle_progress.setProgress((float) progress);
                        circle_progress.setText(String.valueOf(progress) + "%");*/
                    }
                });

        downloadManager.add(downloadRequest);
    }



    public void Follow_unFollow_User(final int position, final Home_Get_Set home_get_set, String fbid){

        String follow_status = home_get_set.follow;
        Log.e("follow_status",follow_status);

        final String send_status;
        if(follow_status.equals("0")){
            send_status="1";
        }else {
            send_status="0";
        }
        Functions.Call_Api_For_Follow_or_unFollow(getActivity(),
                Variables.sharedPreferences.getString(Variables.u_id,""),
                fbid,
                send_status,
                new API_CallBack() {
                    @Override
                    public void ArrayData(ArrayList arrayList) {


                    }

                    @Override
                    public void OnSuccess(String responce) {

                        if(send_status.equals("1")){
                            data_list.remove(position);
                            home_get_set.follow=send_status;
                            data_list.add(position,home_get_set);
                            adapter.notifyDataSetChanged();
                            //  follow_status="1";

                        }
                        else if(send_status.equals("0")){
                            data_list.remove(position);
                            home_get_set.follow=send_status;
                            data_list.add(position,home_get_set);
                            adapter.notifyDataSetChanged();
                            //follow_status="0";
                        }

                        // Call_Api_For_get_Allvideos();
                    }

                    @Override
                    public void OnFail(String responce) {

                    }

                });


    }










    private void ReportPopup(int postion, final Home_Get_Set item) {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.reportpopup);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        // final int selectedId = radioGroup.getCheckedRadioButtonId();
        //final RadioButton genderradioButton = (RadioButton) dialog.findViewById(selectedId);


        TextView yes  = (TextView) dialog.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton=(RadioButton) dialog.findViewById(selectedId);

                if(selectedId==-1){
                    Toast.makeText(getActivity(),"Nothing selected", Toast.LENGTH_SHORT).show();
                }
                else{
                    // Toast.makeText(getActivity(),radioButton.getText(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    //   Functions.Show_loader(getActivity(),false,false);
                    Functions.Call_Api_For_Report_Video(getActivity(), item, String.valueOf(radioButton.getText()), Variables.sharedPreferences.getString(Variables.u_id, ""),
                            new API_CallBack() {
                                @Override
                                public void ArrayData(ArrayList arrayList) {

                                }

                                @Override
                                public void OnSuccess(String responce) {
                                    //  Functions.cancel_loader();
                                    try {
                                        JSONObject jsonObject=new JSONObject(responce);
                                        Toast.makeText(context, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void OnFail(String responce) {
                                    dialog.dismiss();
                                }
                            });



                }
            }
        });


        TextView no  = (TextView) dialog.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }




    // Bottom two function will call the api and get all the videos form api and parse the json data
    private void Call_Api_For_get_Allvideos() {


        Log.d(Variables.tag, MainMenuActivity.token);
        currentPage=-1;

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id,"0"));
            parameters.put("token",MainMenuActivity.token);
            parameters.put("type",type);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.showAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                swiperefresh.setRefreshing(false);
                Parse_data(resp);
            }
        });



    }

    public void Parse_data(String responce){

        data_list=new ArrayList<>();

        try {
            JSONObject jsonObject=new JSONObject(responce);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msgArray=jsonObject.getJSONArray("msg");

                ArrayList<Home_Get_Set> temp_list=new ArrayList();
                for (int i=0;i<msgArray.length();i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item=new Home_Get_Set();
                    item.fb_id=itemdata.optString("fb_id");

                    JSONObject user_info=itemdata.optJSONObject("user_info");

                    item.username=user_info.optString("username");
                    item.first_name=user_info.optString("first_name",context.getResources().getString(R.string.app_name));
                    item.last_name=user_info.optString("last_name","User");
                    item.profile_pic=user_info.optString("profile_pic","null");
                    item.verified=user_info.optString("verified");

                    try {
                        JSONObject follow_data=itemdata.optJSONObject("follow_Status");
                        item.follow= follow_data.optString("follow");
                    } catch (Exception e) {
                        e.printStackTrace();
                        item.follow = "0";
                    }


                    JSONObject sound_data=itemdata.optJSONObject("sound");
                    item.sound_id=sound_data.optString("id");
                    item.sound_name=sound_data.optString("sound_name");
                    item.sound_pic=sound_data.optString("thum");
                    if(sound_data!=null) {
                        JSONObject audio_path = sound_data.optJSONObject("audio_path");
                        item.sound_url_mp3 = audio_path.optString("mp3");
                        item.sound_url_acc = audio_path.optString("acc");
                    }

                    JSONObject count=itemdata.optJSONObject("count");
                    item.like_count=count.optString("like_count");
                    Log.e(TAG, "Parse_data: like count are " +count.optString("like_count") +"  username is "+user_info.optString("username"));
                    item.video_comment_count=count.optString("video_comment_count");


                    item.video_id=itemdata.optString("id");
                    item.liked=itemdata.optString("liked");
                    item.video_url=itemdata.optString("video");


                    item.video_description=itemdata.optString("description");

                    item.thum=itemdata.optString("thum");
                    item.created_date=itemdata.optString("created");

                    temp_list.add(item);
                    Log.e(TAG, "Parse_data: size is "+temp_list.size() );
                }

                if(!temp_list.isEmpty()) {
                    data_list.addAll(temp_list);
                    Set_Adapter();
                }else if(type.equalsIgnoreCase("related")) {
                    type = "following";
                    related_btn.setTextColor(context.getResources().getColor(R.color.graycolor2));
                    following_btn.setTextColor(context.getResources().getColor(R.color.white));
                }else if(type.equalsIgnoreCase("following")){
                    type="related";
                    related_btn.setTextColor(context.getResources().getColor(R.color.white));
                    following_btn.setTextColor(context.getResources().getColor(R.color.graycolor2));
                }

            }else {
                Toast.makeText(context, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void Call_Api_For_Singlevideos(final int postion) {


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id,"0"));
            parameters.put("token",Variables.sharedPreferences.getString(Variables.device_token,"Null"));
            parameters.put("video_id",data_list.get(postion).video_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.Call_Api(context, Variables.showAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                swiperefresh.setRefreshing(false);
                Singal_Video_Parse_data(postion,resp);
            }
        });


    }

    public void Singal_Video_Parse_data(int pos,String responce){

        try {
            JSONObject jsonObject=new JSONObject(responce);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msgArray=jsonObject.getJSONArray("msg");
                for (int i=0;i<msgArray.length();i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item=new Home_Get_Set();
                    item.fb_id=itemdata.optString("fb_id");

                    JSONObject user_info=itemdata.optJSONObject("user_info");

                    item.username=user_info.optString("username");
                    item.first_name=user_info.optString("first_name",context.getResources().getString(R.string.app_name));
                    item.last_name=user_info.optString("last_name","User");
                    item.profile_pic=user_info.optString("profile_pic","null");
                    item.verified=user_info.optString("verified");

                    try {
                        JSONObject follow_data=itemdata.optJSONObject("follow_Status");
                        item.follow=follow_data.optString("follow");
                    } catch (Exception e) {
                        e.printStackTrace();
                        item.follow = "0";
                    }


                    JSONObject sound_data=itemdata.optJSONObject("sound");
                    item.sound_id=sound_data.optString("id");
                    item.sound_name=sound_data.optString("sound_name");
                    item.sound_pic=sound_data.optString("thum");
                    if(sound_data!=null) {
                        JSONObject audio_path = sound_data.optJSONObject("audio_path");
                        item.sound_url_mp3 = audio_path.optString("mp3");
                        item.sound_url_acc = audio_path.optString("acc");
                    }


                    JSONObject count=itemdata.optJSONObject("count");
                    item.like_count=count.optString("like_count");
                    Log.e(TAG, "Singal_Video_Parse_data: all like count "+count.optString("like_count")+" position is "+pos );
                    item.video_comment_count=count.optString("video_comment_count");



                    item.video_id=itemdata.optString("id");
                    item.liked=itemdata.optString("liked");
                    item.video_url=itemdata.optString("video");
                    item.video_description=itemdata.optString("description");

                    item.thum=itemdata.optString("thum");
                    item.created_date=itemdata.optString("created");

                    data_list.remove(pos);
                    data_list.add(pos,item);
                    adapter.notifyDataSetChanged();
                }



            }else {
                Toast.makeText(context, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }





    // this will call when swipe for another video and
    // this function will set the player to the current video
    public void Set_Player(final int currentPage){





          final Home_Get_Set item= data_list.get(currentPage);

           Call_cache();

           HttpProxyCacheServer proxy = TicTic.getProxy(context);
           String proxyUrl = proxy.getProxyUrl(item.video_url);

           DefaultLoadControl loadControl = new DefaultLoadControl.Builder().setBufferDurationsMs(1*1024, 1*1024, 500, 1024).createDefaultLoadControl();

           DefaultTrackSelector trackSelector = new DefaultTrackSelector();
           final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context, trackSelector,loadControl);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));

            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(proxyUrl));

            Log.d(Variables.tag,item.video_url);
            Log.d(Variables.tag,proxyUrl);


             player.prepare(videoSource);

             player.setRepeatMode(Player.REPEAT_MODE_ALL);
             player.addListener(this);


         View layout=layoutManager.findViewByPosition(currentPage);
         final PlayerView playerView=layout.findViewById(R.id.playerview);
          playerView.setPlayer(player);


        player.setPlayWhenReady(is_visible_to_user);
        privious_player=player;





        final RelativeLayout mainlayout = layout.findViewById(R.id.mainlayout);
        playerView.setOnTouchListener(new View.OnTouchListener() {
            int count=0;
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {


//                @Override
//                public boolean onContextClick(MotionEvent e) {
//                    Log.e(TAG, "onContextClick: onContextClick" );
//                    return super.onContextClick(e);
//                }
//
//                @Override
//                public boolean onSingleTapConfirmed(MotionEvent e) {
//                    Log.e(TAG, "onSingleTapConfirmed: " );
//                    return super.onSingleTapConfirmed(e);
//
//                }
//
//                @Override
//                public boolean onDoubleTapEvent(MotionEvent e) {
//                    Log.e(TAG, "onDoubleTapEvent: " );
//                    return super.onDoubleTapEvent(e);
//                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                     super.onFling(e1, e2, velocityX, velocityY);
                    float deltaX = e1.getX() - e2.getX();
                    float deltaXAbs = Math.abs(deltaX);
                    // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
                    if((deltaXAbs > 100) && (deltaXAbs < 1000)) {
                        if(deltaX > 0)
                        {
                            OpenProfile(item,true);
                        }
                    }


                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    super.onSingleTapUp(e);
                    if(!player.getPlayWhenReady()){
                        is_user_stop_video=false;
                        privious_player.setPlayWhenReady(true);
                    }else{
                        is_user_stop_video=true;
                        privious_player.setPlayWhenReady(false);
                    }


                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                    Show_video_option(item);

                }



                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if ((System.currentTimeMillis() - lastFavClick) > 700) {

                        if (!player.getPlayWhenReady()) {
                            is_user_stop_video = false;
                            privious_player.setPlayWhenReady(true);
                        }


                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {


                            if (item.liked.equals("0")) {
                                Show_heart_on_DoubleTap(item, mainlayout, e);
                                Like_Video(currentPage, item);
                            } else {
                                //item.liked ="0";
                                // Show_heart_on_DoubleTap(item, mainlayout, e);
                                Show_always_heart_on_DoubleTap(item, mainlayout, e);


                            }


//                        else if (item.liked.equals("1")){
//                            Show_heart_on_DoubleTap(item, mainlayout, e);
//
//                        }


                        } else {
                            Toast.makeText(context, "Please Login into app", Toast.LENGTH_SHORT).show();
                        }
                    }
                    lastFavClick= System.currentTimeMillis();
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Handler handler = new Handler();
//                int numberOfTaps = 0;
//                long lastTapTimeMs = 0;
//                long touchDownMs = 0;
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        touchDownMs = System.currentTimeMillis();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        handler.removeCallbacksAndMessages(null);
//
//                        if ((System.currentTimeMillis() - touchDownMs) > ViewConfiguration.getTapTimeout()) {
//                            //it was not a tap
//
//                            numberOfTaps = 0;
//                            lastTapTimeMs = 0;
//                            break;
//                        }
//
//                        if (numberOfTaps > 0
//                                && (System.currentTimeMillis() - lastTapTimeMs) < ViewConfiguration.getDoubleTapTimeout()) {
//                            numberOfTaps += 1;
//                        } else {
//                            numberOfTaps = 1;
//                        }
//
//                        lastTapTimeMs = System.currentTimeMillis();
//
//                        if (numberOfTaps == 3) {
//                            Toast.makeText(getApplicationContext(), "triple", Toast.LENGTH_SHORT).show();
//                            //handle triple tap
//                        } else if (numberOfTaps == 2) {
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //handle double tap
//                                    Toast.makeText(getApplicationContext(), "double", Toast.LENGTH_SHORT).show();
//                                }
//                            }, ViewConfiguration.getDoubleTapTimeout());
//                        }
//                }

                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        TextView desc_txt=layout.findViewById(R.id.desc_txt);
        HashTagHelper.Creator.create(context.getResources().getColor(R.color.cyan), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {

                onPause();
                OpenHashtag(hashTag);

            }
        }).handle(desc_txt);



        LinearLayout soundimage = (LinearLayout)layout.findViewById(R.id.sound_image_layout);
        Animation sound_animation = AnimationUtils.loadAnimation(context,R.anim.d_clockwise_rotation);
        soundimage.startAnimation(sound_animation);

        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false))
        Functions.Call_Api_For_update_view(getActivity(),item.video_id);


        swipe_count++;
        if(swipe_count>6){
            Show_add();
            swipe_count=0;
        }



        Call_Api_For_Singlevideos(currentPage);

    }


    public void Call_cache(){
        if(currentPage+1<data_list.size()){
            HttpProxyCacheServer proxy = TicTic.getProxy(context);
            proxy.getProxyUrl(data_list.get(currentPage+1).video_url);

        }
    }


    public void Show_heart_on_DoubleTap(Home_Get_Set item,final RelativeLayout mainlayout,MotionEvent e){


        int x = (int) e.getX()-100;
        int y = (int) e.getY()-100;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        final ImageView iv = new ImageView(getApplicationContext());
        lp.setMargins(x, y, 0, 0);
        iv.setLayoutParams(lp);
        if(item.liked.equals("1"))
        iv.setImageDrawable(getResources().getDrawable(
                R.drawable.ic_like));
        else
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.ic_like_fill));

        mainlayout.addView(iv);
        Animation fadeoutani = AnimationUtils.loadAnimation(context,R.anim.fade_out);

        fadeoutani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainlayout.removeView(iv);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv.startAnimation(fadeoutani);

    }


    private void Show_always_heart_on_DoubleTap(Home_Get_Set item,final RelativeLayout mainlayout,MotionEvent e){

        final ImageView iv = new ImageView(getApplicationContext());
            int x = (int) e.getX()-100;
            int y = (int) e.getY()-100;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(x, y, 0, 0);
            iv.setLayoutParams(lp);



        iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.ic_like_fill));


            mainlayout.addView(iv);







        Animation fadeoutani = AnimationUtils.loadAnimation(context,R.anim.fade_out);

        fadeoutani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                    mainlayout.removeView(iv);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

            iv.startAnimation(fadeoutani);


    }

    public void Show_add(){
        if(mInterstitialAd!=null && mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }


    @Override
    public void onDataSent(String yourData) {
        int comment_count =Integer.parseInt(yourData);
        Home_Get_Set item=data_list.get(currentPage);
        item.video_comment_count=""+comment_count;
        data_list.remove(currentPage);
        data_list.add(currentPage,item);
        adapter.notifyDataSetChanged();
    }



    // this will call when go to the home tab From other tab.
    // this is very importent when for video play and pause when the focus is changes
    boolean is_visible_to_user;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        is_visible_to_user=isVisibleToUser;

        if(privious_player!=null && (isVisibleToUser && !is_user_stop_video)){
            privious_player.setPlayWhenReady(true);
        }else if(privious_player!=null && !isVisibleToUser){
            privious_player.setPlayWhenReady(false);
        }
    }



   // when we swipe for another video this will relaese the privious player
    SimpleExoPlayer privious_player;
    public void Release_Privious_Player(){
        if(privious_player!=null) {
            privious_player.removeListener(this);
            privious_player.release();
        }
    }




    // this function will call for like the video and Call an Api for like the video
    public void Like_Video(final int position, final Home_Get_Set home_get_set){
        String action=home_get_set.liked;
        Log.e(TAG, "Like_Video: action is "+action );
        Log.e(TAG, "Like_Video: total like count "+home_get_set.like_count );

        if(action.equals("1")){
            action="0";
            home_get_set.like_count=""+(Integer.parseInt(home_get_set.like_count) -1);
        }else {
            action="1";
            home_get_set.like_count=""+(Integer.parseInt(home_get_set.like_count) +1);
        }


        data_list.remove(position);
        home_get_set.liked=action;
        data_list.add(position,home_get_set);
        Log.e(TAG, "Like_Video:  home_get_set "+home_get_set.toString() );


        adapter.notifyDataSetChanged();
      //  adapter.notifyItemChanged(position);

        Functions.Call_Api_For_like_video(getActivity(), home_get_set.video_id, action,new API_CallBack() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnSuccess(String responce) {

            }

            @Override
            public void OnFail(String responce) {

            }
        });


    }



    // this will open the comment screen
    private void OpenComment(Home_Get_Set item) {

        int comment_counnt=Integer.parseInt(item.video_comment_count);

        Fragment_Data_Send fragment_data_send=this;

        Comment_F comment_f = new Comment_F(comment_counnt,fragment_data_send);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        Bundle args = new Bundle();
        args.putString("video_id",item.video_id);
        args.putString("user_id",item.fb_id);
        comment_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, comment_f).commit();


    }


    public void Open_Login(){
        Intent intent = new Intent(getActivity(), Login_A.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    // this will open the profile of user which have uploaded the currenlty running video
    private void OpenProfile(Home_Get_Set item,boolean from_right_to_left) {
        if(Variables.sharedPreferences.getString(Variables.u_id,"0").equals(item.fb_id)){

            TabLayout.Tab profile= MainMenuFragment.tabLayout.getTabAt(4);
            profile.select();

        }else {
            Profile_F profile_f = new Profile_F(new Fragment_Callback() {
                @Override
                public void Responce(Bundle bundle) {
                    Call_Api_For_Singlevideos(currentPage);
                }
            });
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            if(from_right_to_left)
            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            else
                transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);

            Bundle args = new Bundle();
            args.putString("user_id", item.fb_id);
            args.putString("user_name",item.first_name+" "+item.last_name);
            args.putString("user_pic",item.profile_pic);
            profile_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, profile_f).commit();
        }

    }


    // this will open the profile of user which have uploaded the currenlty running video
    private void OpenHashtag(String tag) {

            Taged_Videos_F taged_videos_f = new Taged_Videos_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("tag", tag);
            taged_videos_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, taged_videos_f).commit();


    }



    private void Show_video_option(final Home_Get_Set home_get_set) {

        final CharSequence[] options = { "Save Video","Cancel" };

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context,R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Save Video"))

                {
                    if(Functions.Checkstoragepermision(getActivity()))
                    Save_Video(home_get_set, false);

                }


                else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }




    public void Save_Video(final Home_Get_Set item, final boolean isDuet){

        JSONObject params=new JSONObject();
        try {
            params.put("video_id",item.video_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context,false,false);
        ApiRequest.Call_Api(context, Variables.downloadFile, params, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    Log.e(TAG, "Responce: downloading "+resp );
                    JSONObject responce=new JSONObject(resp);
                    String code=responce.optString("code");
                    if(code.equals("200")){
                        JSONArray msg=responce.optJSONArray("msg");
                        JSONObject jsonObject=msg.optJSONObject(0);
                        String download_url=jsonObject.getString("download_url");

                        if(download_url!=null){

                            Functions.Show_determinent_loader(context,false,false);
                            PRDownloader.initialize(getActivity().getApplicationContext());
                            DownloadRequest prDownloader= PRDownloader.download(download_url, Variables.app_folder, item.video_id+"no_watermark"+".mp4")
                                    .build()
                                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                        @Override
                                        public void onStartOrResume() {

                                        }
                                    })
                                    .setOnPauseListener(new OnPauseListener() {
                                        @Override
                                        public void onPause() {

                                        }
                                    })
                                    .setOnCancelListener(new OnCancelListener() {
                                        @Override
                                        public void onCancel() {

                                        }
                                    })
                                    .setOnProgressListener(new OnProgressListener() {
                                        @Override
                                        public void onProgress(Progress progress) {

                                            int prog=(int)((progress.currentBytes*100)/progress.totalBytes);
                                            Functions.Show_loading_progress(prog);

                                        }
                                    });


                            prDownloader.start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    Functions.cancel_determinent_loader();
                                    if (isDuet){
                                        Functions.cancel_determinent_loader();
                                        File file = new File(Variables.app_folder, item.video_id+"no_watermark"+".mp4");
                                        startDuetRecording(file.getPath());
                                    }else {
                                        Scan_file(item);
                                        Applywatermarks(item);
                                    }
                                }

                                @Override
                                public void onError(Error error) {
                                    Delete_file_no_watermark(item);
                                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                    Functions.cancel_determinent_loader();
                                }


                            });

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }


    public void Delete_file_no_watermark(Home_Get_Set item){
        File file=new File(Variables.app_folder+item.video_id+"no_watermark"+".mp4");
        if(file.exists()){
            file.delete();
        }
    }

    public void Scan_file(Home_Get_Set item){
        MediaScannerConnection.scanFile(getActivity(),
                new String[] { Variables.app_folder+item.video_id+".mp4" },
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {

                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                        Toast.makeText(context, "Video Downloaded", Toast.LENGTH_SHORT).show();


                    }
                });


    }

    private void Applywatermark(final String videoId) {

        Functions.Show_determinent_loader(context, false, false);
        Bitmap myLogo = ((BitmapDrawable) getResources().getDrawable(R.drawable.water_iv)).getBitmap();
        myLogo = Bitmap.createScaledBitmap(myLogo, 150, 53, false);
        myLogo = Functions.addStampToImage(context, myLogo,  ""+TicTic.videoUsername);
        GlWatermarkFilter filter = new GlWatermarkFilter(myLogo, GlWatermarkFilter.Position.LEFT_TOP);
        new Mp4Composer(Environment.getExternalStorageDirectory() + "/Jagmee/" + videoId + "no_watermark" + ".mp4",
                Environment.getExternalStorageDirectory() + "/Jagmee/" + videoId + ".mp4")
                //.rotation(Rotation.ROTATION_90)
                .filter(filter)
                .size(540, 960)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                //.trim(200, 5000)
                .listener(new Mp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        Functions.Show_loading_progress((int) ((progress * 100) / 2) + 50);
                    }

                    @Override
                    public void onCurrentWrittenVideoTime(long timeUs) {

                    }

                    @Override
                    public void onCompleted() {
                        runOnUiThread(() -> {
                            Functions.cancel_determinent_loader();
                            Delete_file_no_watermark(videoId);
                            Scan_file(videoId);
                        });
                    }

                    @Override
                    public void onCanceled() {
                    }

                    @Override
                    public void onFailed(Exception exception) {
                    }
                })
                .start();

        /*Functions.Show_determinent_loader(context, false, false);

        Bitmap myLogo = ((BitmapDrawable) getResources().getDrawable(R.drawable.water_iv)).getBitmap();
        myLogo = Bitmap.createScaledBitmap(myLogo, 180, 90, false);
        myLogo = Functions.addStampToImage(context, myLogo,  "   "+TicTic.videoUsername);


        //myLogo = Functions.drawTextToBitmap(context,)

        // myLogo = Functions.waterMark(myLogo, TicTic.videoUsername, null, Color.RED, 90, 90, true);

//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        params.setMargins(left, top, right, bottom);
//        myLogo.setLayoutParams(params);




        GlWatermarkFilter filter = new GlWatermarkFilter(myLogo, GlWatermarkFilter.Position.LEFT_TOP);

        new GPUMp4Composer(Environment.getExternalStorageDirectory() + "/Jagmee/" + videoId + "no_watermark" + ".mp4",
                Environment.getExternalStorageDirectory() + "/Jagmee/" + videoId + ".mp4")
                .filter(filter)
                .size(540, 960)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .listener(new GPUMp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {

                        Log.d("resp", "" + (int) (progress * 100));
                        Functions.Show_loading_progress((int) ((progress * 100) / 2) + 50);

                    }

                    @Override
                    public void onCompleted() {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Functions.cancel_determinent_loader();
                                Delete_file_no_watermark(videoId);
                                Scan_file(videoId);
                            }
                        });
                    }

                    @Override
                    public void onCanceled() {
                        Log.d("resp", "onCanceled");
                    }

                    @Override
                    public void onFailed(Exception exception) {

                        Log.e("resp", exception.toString());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    Delete_file_no_watermark(videoId);
                                    Functions.cancel_determinent_loader();
                                    Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {

                                }
                            }
                        });

                    }
                })
                .start();*/
    }

    private void Delete_file_no_watermark(String video_id) {
        File file = new File(Environment.getExternalStorageDirectory() + "/Jagmee/" + video_id + "no_watermark" + ".mp4");
        if (file.exists()) {
            file.delete();
        }
    }

    private void Scan_file(String videoId) {
        MediaScannerConnection.scanFile(getActivity(),
                new String[]{Environment.getExternalStorageDirectory() + "/Jagmee/" + videoId + ".mp4"},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {

                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);


                        File yourFile = new File(String.valueOf(path));

                        // String outputFileName = appendTwoVideos(yourFile.getAbsolutePath(), yourFile.getAbsolutePath());
                        //Log.e(TAG, "onScanCompleted: outputFileName " + outputFileName);


                        try {
                            Log.e(TAG, "onDownloadComplete: file path "+yourFile.getAbsolutePath() +" video Id "+TicTic.videoId);
                            scanFile(yourFile.getAbsolutePath());
                        } catch (Exception e) {
                        }


                    }
                });
    }

    private void scanFile(String path) {

        MediaScannerConnection.scanFile(context,
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        // Log.e("TAG", "Finished scanning " + path);


                    }
                });
    }

    public void Applywatermarks(final Home_Get_Set item){
        /*Functions.Show_determinent_loader(context, false, false);


        Bitmap myLogo = ((BitmapDrawable)getResources().getDrawable(R.drawable.water_iv)).getBitmap();
        myLogo=Bitmap.createScaledBitmap(myLogo, 180, 90, false);
        myLogo = Functions.addStampToImage(context, myLogo,  "   "+item.username);
        GlWatermarkFilter filter=new GlWatermarkFilter(myLogo, GlWatermarkFilter.Position.LEFT_TOP);
        new GPUMp4Composer(Variables.app_folder+item.video_id+"no_watermark"+".mp4",
                Variables.app_folder+item.video_id+".mp4")
                .filter(filter)
                .size(540, 960)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .listener(new GPUMp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {

                        Log.d("resp", "" + (int) (progress * 100));
                        Functions.Show_loading_progress((int) ((progress * 100) / 2) + 50);

                    }

                    @Override
                    public void onCompleted() {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Functions.cancel_determinent_loader();
                                Delete_file_no_watermark(item);
                                Scan_file(item);
                            }
                        });
                    }

                    @Override
                    public void onCanceled() {
                        Log.d("resp", "onCanceled");
                    }

                    @Override
                    public void onFailed(Exception exception) {

                        Log.d("resp",exception.toString());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    Delete_file_no_watermark(item);
                                    Functions.cancel_determinent_loader();
                                    Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();

                                }catch (Exception e){

                                }
                            }
                        });

                    }
                })
                .start();*/
        Functions.Show_determinent_loader(context, false, false);
        Bitmap myLogo = ((BitmapDrawable) getResources().getDrawable(R.drawable.water_iv)).getBitmap();
        myLogo = Bitmap.createScaledBitmap(myLogo, 90, 90, false);
        myLogo = Functions.addStampToImage(context, myLogo,  "   "+TicTic.videoUsername);
        GlWatermarkFilter filter = new GlWatermarkFilter(myLogo, GlWatermarkFilter.Position.LEFT_TOP);
        new Mp4Composer(Environment.getExternalStorageDirectory() + "/Jagmee/" + item.video_id + "no_watermark" + ".mp4",
                Environment.getExternalStorageDirectory() + "/Jagmee/" + item.video_id + ".mp4")
                //.rotation(Rotation.ROTATION_90)
                .filter(new GlFilterGroup(new GlMonochromeFilter(), new GlVignetteFilter()))
                .filter(filter)
                .size(540, 960)
                //.fillMode(FillMode)
                //.trim(200, 5000)
                .listener(new Mp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        Functions.Show_loading_progress((int) ((progress * 100) / 2) + 50);
                    }

                    @Override
                    public void onCurrentWrittenVideoTime(long timeUs) {

                    }

                    @Override
                    public void onCompleted() {
                        runOnUiThread(() -> {
                            Functions.cancel_determinent_loader();
                            Delete_file_no_watermark(item.video_id);
                            Scan_file(item.video_id);
                        });
                    }

                    @Override
                    public void onCanceled() {
                    }

                    @Override
                    public void onFailed(Exception exception) {
                    }
                })
                .start();
    }


    public void startDuetRecording(String filePath){
        int gop = 30;
        AliyunSnapVideoParam mAliyunSnapVideoParam = new AliyunSnapVideoParam.Builder()
                .setResulutionMode(AliyunSnapVideoParam.RESOLUTION_360P)
                .setRatioMode(AliyunSnapVideoParam.RATIO_MODE_9_16)
                .setGop(gop)
                .setVideoCodec(VideoCodecs.H264_SOFT_FFMPEG)
                .setFrameRate(30)
                .setCropMode(VideoDisplayMode.FILL)
                .setVideoQuality(VideoQuality.HD)
                .build();
        AlivcSvideoMixRecordActivity.startMixRecord(getActivity(), mAliyunSnapVideoParam, filePath, RenderingMode.FaceUnity, false);
    }


    public boolean is_fragment_exits(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if(fm.getBackStackEntryCount()==0){
            return false;
        }else {
            return true;
        }

    }

    // this is lifecyle of the Activity which is importent for play,pause video or relaese the player
    @Override
    public void onResume() {
        super.onResume();
        if((privious_player!=null && (is_visible_to_user && !is_user_stop_video)) && !is_fragment_exits() ){
            privious_player.setPlayWhenReady(true);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if(privious_player!=null){
            privious_player.setPlayWhenReady(false);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if(privious_player!=null){
            privious_player.setPlayWhenReady(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(privious_player!=null){
            privious_player.release();
        }

        if(mReceiver!=null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }



    public boolean check_permissions() {

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(context, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 2);
        }else {

            return true;
        }

        return false;
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }





    // Bottom all the function and the Call back listener of the Expo player
    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }


    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }


    @Override
    public void onLoadingChanged(boolean isLoading) {

    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if(playbackState==Player.STATE_BUFFERING){
            p_bar.setVisibility(View.VISIBLE);
        }
        else if(playbackState==Player.STATE_READY){
             p_bar.setVisibility(View.GONE);
        }


    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    @Override
    public void onSeekProcessed() {

    }



}
