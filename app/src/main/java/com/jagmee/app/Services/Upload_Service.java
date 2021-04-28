package com.jagmee.app.Services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import android.util.Log;

import com.jagmee.app.Main_Menu.MainMenuActivity;
import com.jagmee.app.SimpleClasses.Callback;
import com.jagmee.app.SimpleClasses.Functions;
import com.jagmee.app.SimpleClasses.MultiPartRequest;
import com.jagmee.app.SimpleClasses.Variables;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by AQEEL on 6/7/2018.
 */



// this the background service which will upload the video into database
public class Upload_Service extends Service{
    private static final String TAG = "Upload_Service";



    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public Upload_Service getService() {
            return Upload_Service.this;
        }
    }

    boolean mAllowRebind;
    ServiceCallback Callback;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }



    String draft_file;
    String videopath;
    String description;
    String privacy_type;
    String allow_comment;
    SharedPreferences sharedPreferences;

    public Upload_Service() {
        super();
    }

    public Upload_Service(ServiceCallback serviceCallback) {
        Callback=serviceCallback;
       }

    public void setCallbacks(ServiceCallback serviceCallback){
        Callback=serviceCallback;
    }


    @Override
    public void onCreate() {
        sharedPreferences=getSharedPreferences(Variables.pref_name,MODE_PRIVATE);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent!=null){
        if (intent.getAction().equals("startservice")) {
            showNotification();

            videopath =intent.getStringExtra("uri");
            draft_file=intent.getStringExtra("draft_file");
            description=intent.getStringExtra("desc");
            privacy_type=intent.getStringExtra("privacy_type");
            allow_comment=intent.getStringExtra("allow_comment");

            new Thread(new Runnable() {
                @Override
                public void run() {

                    MultiPartRequest request=new MultiPartRequest(Upload_Service.this, new Callback() {
                        @Override
                        public void Responce(String resp) {
                            Log.e(TAG, "Responce: "+resp );
                            if(!Variables.is_secure_info)
                                Log.d(Variables.tag,resp);
                            try {
                                JSONObject jsonObject=new JSONObject(resp);
                                String code=jsonObject.optString("code");
                                if(code.equalsIgnoreCase("200")){
                                    Variables.Reload_my_videos=true;
                                    Variables.Reload_my_videos_inner=true;
                                    Delete_draft_file();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            stopForeground(true);
                            stopSelf();
                            sendBroadcast(new Intent("uploadVideo"));
                            sendBroadcast(new Intent("newVideo"));
                        }
                    });
                    request.addString("fb_id", Variables.user_id);
                    request.addString("sound_id", Variables.Selected_sound_id);
                    request.addString("description", description);
                    request.addString("privacy_type", privacy_type);
                    request.addString("allow_comment",allow_comment);
                    request.addvideoFile("uploaded_file", videopath,Functions.getRandomString()+".mp4");
                    Log.e(TAG, "run: fb_id "+Variables.user_id+" soundId "+Variables.Selected_sound_id+" description "+description
                    +" privacy type "+privacy_type+" allow comt "+allow_comment+" upload file "+videopath);
                    request.execute();
                }
            }).start();
        }


        else if(intent.getAction().equals("stopservice")){
            stopForeground(true);
            stopSelf();
           }

        }

        return Service.START_STICKY;
    }


    // this will show the sticky notification during uploading video
    private void showNotification() {

        Intent notificationIntent = new Intent(this, MainMenuActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        final String CHANNEL_ID = "default";
        final String CHANNEL_NAME = "Default";

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel defaultChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(defaultChannel);
        }

        androidx.core.app.NotificationCompat.Builder builder = (androidx.core.app.NotificationCompat.Builder) new androidx.core.app.NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setContentTitle("Uploading Video")
                .setContentText("Please wait! Video is uploading....")
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        android.R.drawable.stat_sys_upload))
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        startForeground(101, notification);
    }

    public void Delete_draft_file(){
        try {
            if(draft_file!=null) {
                File file = new File(draft_file);
                file.delete();
            }
        }catch (Exception e){

        }
    }
}