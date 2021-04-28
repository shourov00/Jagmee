package com.jagmee.app.SimpleClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.jagmee.app.R;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class MultiPartRequest {

    public Context context;
    public MultipartBuilder builder;
    private OkHttpClient client;
    private Callback callBack;
    SharedPreferences sharedPreferences;

    public MultiPartRequest(Context context, Callback callback) {
        this.sharedPreferences=context.getSharedPreferences(Variables.pref_name,Context.MODE_PRIVATE);
        this.context = context;
        this.builder = new MultipartBuilder();
        this.builder.type(MultipartBuilder.FORM);
        this.client = new OkHttpClient();
        client.setConnectTimeout(20, TimeUnit.SECONDS);
        client.setWriteTimeout(5,TimeUnit.MINUTES);
        client.setReadTimeout(5,TimeUnit.MINUTES);
        this.callBack=callback;
    }

    public void addString(String name, String value) {
        this.builder.addFormDataPart(name, value);
    }

    

    public void addvideoFile(String name, String filePath, String fileName) {
        this.builder.addFormDataPart(name, fileName, RequestBody.create(
                MediaType.parse("video/mp4"), new File(filePath)));
    }
   

    public String execute() {
        new send().execute();
        return "";
    }

    public class send extends AsyncTask<MultiPartRequest, Void, String>{


        @Override
        protected String doInBackground(MultiPartRequest... multiPartRequests) {
            RequestBody requestBody = null;
            Request request = null;
            Response response = null;


            String strResponse = null;

            try {
                Headers.Builder headerBuilder = new Headers.Builder();
                headerBuilder.add("fb-id",sharedPreferences.getString(Variables.u_id,"0"));
                headerBuilder.add("version", context.getString(R.string.version));
                headerBuilder.add("device",  context.getResources().getString(R.string.device));
                headerBuilder.add("tokon", sharedPreferences.getString(Variables.api_token,""));
                headerBuilder.add("deviceid", sharedPreferences.getString(Variables.device_id,""));

                Log.d(Variables.tag,headerBuilder.toString());
                Log.d(Variables.tag,Variables.uploadVideo);

                requestBody = builder.build();
                request = new Request.Builder()
                        .url(Variables.uploadVideo).post(requestBody)
                        .headers(headerBuilder.build())
                        .build();


                response = client.newCall(request).execute();

                Log.d(Variables.tag,response.toString());

                if (!response.isSuccessful())
                    throw new IOException();
                
                if (response.isSuccessful()) {
                    strResponse = response.body().string();
                    Log.d(Variables.tag,strResponse);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(Variables.tag,e.toString());
            } finally {
                builder = null;
                if (client != null)
                    client = null;
                System.gc();
            }
            return strResponse;
        }

        @Override
        protected void onPostExecute(String response) {

        if (response != null)
            callBack.Responce(response);
        else
            callBack.Responce("");

        Log.d(Variables.tag,response);
        super.onPostExecute(response);
    }
}

}

