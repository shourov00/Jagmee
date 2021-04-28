package com.jagmee.app.SoundLists;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.svideo.recorder.activity.AlivcSvideoRecordActivity;
import com.aliyun.svideo.recorder.bean.AlivcRecordInputParam;
import com.aliyun.svideo.recorder.bean.RenderingMode;
import com.google.android.exoplayer2.util.Log;
import com.jagmee.app.Home.Home_Get_Set;
import com.jagmee.app.R;
import com.jagmee.app.SimpleClasses.Functions;
import com.jagmee.app.SimpleClasses.Variables;
import com.jagmee.app.Video_Recording.Video_Recoder_A;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.jagmee.app.alivcsolution.constants.LittleVideoParamConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

public class VideoSound_A extends AppCompatActivity implements View.OnClickListener {

    Home_Get_Set item;
    TextView sound_name,description_txt;
    ImageView sound_image;

    File audio_file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_sound);

        Intent intent=getIntent();
        if(intent.hasExtra("data")){
            item= (Home_Get_Set) intent.getSerializableExtra("data");
        }


        sound_name=findViewById(R.id.sound_name);
        description_txt=findViewById(R.id.description_txt);
        sound_image=findViewById(R.id.sound_image);

        if((item.sound_name==null || item.sound_name.equals("") || item.sound_name.equals("null"))){
           sound_name.setText("original sound - "+item.first_name+" "+item.last_name);
        }else {
           sound_name.setText(item.sound_name);
        }
        description_txt.setText(item.video_description);


        findViewById(R.id.back_btn).setOnClickListener(this);

        findViewById(R.id.save_btn).setOnClickListener(this);
        findViewById(R.id.create_btn).setOnClickListener(this);

        findViewById(R.id.play_btn).setOnClickListener(this);
        findViewById(R.id.pause_btn).setOnClickListener(this);


        Uri uri = Uri.parse(item.thum);
        sound_image.setImageURI(uri);

        Log.d(Variables.tag,item.thum);
        Log.d(Variables.tag,item.sound_url_acc);

        Save_Audio();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.back_btn:
                finish();
                break;
            case R.id.save_btn:
                if(audio_file!=null && audio_file.exists()) {
                    // String appName = getResources().getResourceName(R.string.app_name)
                    try {
                        Log.e("PATH VALUE", Variables.app_folder+item.video_id+".mp3");
                        copyFile(audio_file, new File(Variables.app_folder+item.video_id+".mp3"));
                        Toast.makeText(this, "Audio Saved", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.create_btn:
                if(audio_file!=null && audio_file.exists()) {
                    StopPlaying();
                    Convert_Mp3_to_acc();
                    /*try {
                        Log.e("TAG", Variables.app_folder+getResources().getResourceName(R.string.app_name)+" "+item.video_id+".mp3");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Log.e("TAG", Variables.app_folder+getResources().getResourceName(R.string.app_name)+" "+item.video_id+".aac");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    //startRecording(Variables.app_folder+getResources().getResourceName(R.string.app_name)+" "+item.video_id+".mp3");

                }
                break;

            case R.id.play_btn:
                if(audio_file!=null && audio_file.exists())
                    playaudio();

                break;

            case R.id.pause_btn:
                StopPlaying();
                break;
        }
    }



    SimpleExoPlayer player;
    public void playaudio(){

            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, "TikTok"));

            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.fromFile(audio_file));


            player.prepare(videoSource);
            player.setPlayWhenReady(true);

          Show_playing_state();
        }




    public void StopPlaying(){
        if(player!=null){
            player.setPlayWhenReady(false);
        }
        Show_pause_state();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(player!=null){
            player.setPlayWhenReady(false);
        }
        Show_pause_state();
    }



    public void Show_playing_state(){
        findViewById(R.id.play_btn).setVisibility(View.GONE);
        findViewById(R.id.pause_btn).setVisibility(View.VISIBLE);
    }

    public void Show_pause_state(){
        findViewById(R.id.play_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.pause_btn).setVisibility(View.GONE);
    }

    DownloadRequest prDownloader;
    ProgressDialog progressDialog;
    public void Save_Audio(){
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        prDownloader= PRDownloader.download(item.sound_url_acc, Variables.app_folder, Variables.SelectedAudio_AAC)
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

                    }
                });

        prDownloader.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                progressDialog.dismiss();
                audio_file=new File( Variables.app_folder+ Variables.SelectedAudio_AAC);
                 }

            @Override
            public void onError(Error error) {
                progressDialog.dismiss();
            }
        });


    }


    public void Open_video_recording(){
        Intent intent = new Intent(VideoSound_A.this, Video_Recoder_A.class);
        intent.putExtra("sound_name",sound_name.getText().toString());
        intent.putExtra("sound_id",item.sound_id);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

    }


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }




    public void Convert_Mp3_to_acc(){
        Functions.Show_loader(this,false,false);
        File flacFile = new File(Variables.app_folder+item.video_id+".mp3");
        if (flacFile.exists()) {
            startRecording(Variables.app_folder+item.video_id+".mp3");

        } else {


        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // So fast? Love it!
                Functions.cancel_loader();
                startRecording(Variables.app_folder+item.video_id+".mp3");
            }
            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong
                Functions.cancel_loader();
                Toast.makeText(VideoSound_A.this, "Please Save Audio First", Toast.LENGTH_SHORT).show();
            }
        };
        AndroidAudioConverter.with(this)
                // Your current audio file
                .setFile(flacFile)
                // Your desired audio format
                .setFormat(AudioFormat.MP3)
                // An callback to know when conversion is finished
                .setCallback(callback)
                // Start conversion
                .convert();
        }
        /*AndroidAudioConverter.load(VideoSound_A.this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                IConvertCallback callback = new IConvertCallback() {
                    @Override
                    public void onSuccess(File convertedFile) {
                        Functions.cancel_loader();
                        startRecording(Variables.app_folder+getResources().getResourceName(R.string.app_name)+" "+item.video_id+".mp3");
                        //  Open_video_recording();
                    }
                    @Override
                    public void onFailure(Exception error) {
                        Functions.cancel_loader();
                        Toast.makeText(VideoSound_A.this, "Please Save Audio First", Toast.LENGTH_SHORT).show();
                       // Toast.makeText(VideoSound_A.this, "Save Audio First"+error, Toast.LENGTH_SHORT).show();
                    }
                };
                AndroidAudioConverter.with(VideoSound_A.this)
                        .setFile(flacFile)
                        .setFormat(AudioFormat.AAC)
                        .setCallback(callback)
                        .convert();
            }

            @Override
            public void onFailure(Exception error) {
                Functions.cancel_loader();
            }
        });*/
    }



    public void startRecording(String musicUri){
        final AlivcRecordInputParam recordInputParam = new AlivcRecordInputParam.Builder()
                .setResolutionMode(LittleVideoParamConfig.Recorder.RESOLUTION_MODE)
                .setRatioMode(LittleVideoParamConfig.Recorder.RATIO_MODE)
                .setMaxDuration(LittleVideoParamConfig.Recorder.MAX_DURATION)
                .setMinDuration(LittleVideoParamConfig.Recorder.MIN_DURATION)
                .setVideoQuality(LittleVideoParamConfig.Recorder.VIDEO_QUALITY)
                .setGop(LittleVideoParamConfig.Recorder.GOP)
                .setVideoCodec(LittleVideoParamConfig.Recorder.VIDEO_CODEC)
                .setVideoRenderingMode(RenderingMode.Race)
                .build();
        AlivcSvideoRecordActivity.startRecordWithMusic(this, recordInputParam, musicUri, sound_name.getText().toString());
    }
}
