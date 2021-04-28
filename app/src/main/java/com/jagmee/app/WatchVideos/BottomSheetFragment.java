package com.jagmee.app.WatchVideos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.daasuu.mp4compose.FillMode;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.daasuu.mp4compose.filter.GlFilterGroup;
import com.daasuu.mp4compose.filter.GlMonochromeFilter;
import com.daasuu.mp4compose.filter.GlVignetteFilter;
import com.daasuu.mp4compose.filter.GlWatermarkFilter;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.jagmee.app.R;
import com.jagmee.app.SimpleClasses.ApiRequest;
import com.jagmee.app.SimpleClasses.Callback;
import com.jagmee.app.SimpleClasses.Functions;
import com.jagmee.app.SimpleClasses.TicTic;
import com.jagmee.app.SimpleClasses.Variables;
import com.jagmee.app.Video_Recording.AnimatedGifEncoder;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

import static com.aliyun.svideo.common.utils.ThreadUtils.runOnUiThread;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private static final String TAG = "BottomSheetFragment";

    // new
    Context context;

    public DonutProgress circle_progress;

    private static final String TAG_WHATSAPP_COUNTER = "whatsapp_counter";

    private ThinDownloadManager downloadManager;

    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;

    private TextView pleasewait;

    public String action;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootViewone = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);

        circle_progress = (DonutProgress) rootViewone.findViewById(R.id.circle_progress);
        pleasewait = (TextView) rootViewone.findViewById(R.id.pleasewait);

        ((CardView) rootViewone.findViewById(R.id.download)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "download";
                try {
                    fbdownloadvideo(TicTic.downloadurl);
                } catch (Exception ed) {
                    Toast.makeText(context, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                    circle_progress.setVisibility(View.GONE);
                }
            }
        });
        ((CardView) rootViewone.findViewById(R.id.whtsp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                action = "whatsapp";
                try {
                    Log.e(TAG, "onClick: url without trim " + TicTic.downloadurl);
                    Log.e(TAG, "onClick: url with trim " + TicTic.downloadurl.trim());

                    fbdownloadvideo(TicTic.downloadurl);

                } catch (Exception ed) {
                    Toast.makeText(context, "Something went wrong. Try again." + ed, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onClick: downloading error " + ed);
                    circle_progress.setVisibility(View.GONE);
                }
            }
        });


        ((CardView) rootViewone.findViewById(R.id.copylink)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboard(context, TicTic.shareurl);
            }
        });

        ((CardView) rootViewone.findViewById(R.id.fb)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "facebook";
                try {
                    fbdownloadvideo(TicTic.downloadurl);
                } catch (Exception ed) {
                    Toast.makeText(context, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                    circle_progress.setVisibility(View.GONE);
                }
            }
        });

        ((CardView) rootViewone.findViewById(R.id.other)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "other";
                try {
                    fbdownloadvideo(TicTic.downloadurl);
                } catch (Exception ed) {
                    Toast.makeText(context, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                    circle_progress.setVisibility(View.GONE);
                }
            }
        });

        ((CardView) rootViewone.findViewById(R.id.insta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "instagram";
                try {
                    fbdownloadvideo(TicTic.downloadurl);
                } catch (Exception ed) {
                    Toast.makeText(context, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                    circle_progress.setVisibility(View.GONE);
//                    subrel1ly.setVisibility(View.GONE);
                }
            }
        });

        ((CardView) rootViewone.findViewById(R.id.twitter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "twitter";
                try {
                    fbdownloadvideo(TicTic.downloadurl);
                } catch (Exception ed) {
                    Toast.makeText(context, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                    circle_progress.setVisibility(View.GONE);
                }
            }
        });

        return rootViewone;
    }

    private void fbdownloadvideo(final String videoUrl) {
        downloadvideo(videoUrl);
        //Save_Video(videoUrl);
    }


    /*public void Save_Video(final String video_id) {
        Log.e(TAG, "Save_Video: video_id " + video_id);

        System.out.println("Rajan_videoUrl" + video_id);
        circle_progress.setVisibility(View.VISIBLE);
        pleasewait.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
        try {
            params.put("video_id", video_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context, false, false);
        ApiRequest.Call_Api(context, Variables.downloadFile, params, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    Log.e(TAG, "Responce: downloading " + resp);
                    JSONObject responce = new JSONObject(resp);
                    String code = responce.optString("code");
                    if (code.equals("200")) {
                        JSONArray msg = responce.optJSONArray("msg");
                        JSONObject jsonObject = msg.optJSONObject(0);
                        String download_url = jsonObject.getString("download_url");

                        if (download_url != null) {

                            Functions.Show_determinent_loader(context, false, false);
                            PRDownloader.initialize(getActivity().getApplicationContext());
                            com.downloader.request.DownloadRequest prDownloader = PRDownloader.download(download_url, Variables.app_folder, video_id + "no_watermark" + ".mp4")
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

                                            int prog = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                                            Functions.Show_loading_progress(prog);

                                        }
                                    });


                            prDownloader.start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    Functions.cancel_determinent_loader();
                                    // Scan_file(item);
                                    Applywatermark(video_id);
                                }

                                @Override
                                public void onError(Error error) {
                                    Delete_file_no_watermark(video_id);
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

    }*/


    private void downloadvideo(String videoUrl) {
        System.out.println("Rajan_videoUrl" + videoUrl);
        circle_progress.setVisibility(View.VISIBLE);
        pleasewait.setVisibility(View.VISIBLE);

        Uri downloadUri = Uri.parse(videoUrl);
        final File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Jagmee");
        if (!file.exists()) {
            file.mkdirs();
        }
//        Random r = new Random();
//        final int i1 = r.nextInt();

        final Uri destinationUri = Uri.parse(file + File.separator + TicTic.videoId + "no_watermark" + ".mp4");
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .addCustomHeader("Auth-Token", "YourTokenApiKey")
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadContext("downloader")//Optional
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        Toast.makeText(context, "Video Downloaded", Toast.LENGTH_LONG).show();
                        circle_progress.setVisibility(View.GONE);
                        circle_progress.setProgress((float) 0);
                        circle_progress.setText(String.valueOf(0) + "%");
                        pleasewait.setVisibility(View.GONE);

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
                                sendtowhatsapp(yourFile.getAbsolutePath());
                            } else if (action.contains("facebook")) {
                                sendtofacebook(yourFile.getAbsolutePath());
                            } else if (action.contains("other")) {
                                sendtoother(yourFile.getAbsolutePath());
                            } else if (action.contains("instagram")) {
                                sendtoinstagram(yourFile.getAbsolutePath());
                            } else if (action.contains("twitter")) {
                                sendtotwitter(yourFile.getAbsolutePath());
                            }
                        } else {
                            Applywatermark(TicTic.videoId);
                        }
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        Toast.makeText(context, "Download Failed", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onDownloadFailed: " + errorMessage);
                        circle_progress.setVisibility(View.GONE);
                        circle_progress.setProgress((float) 0);
                        circle_progress.setText(String.valueOf(0) + "%");
                        pleasewait.setVisibility(View.GONE);
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
//                        System.out.println("Meera_onProgress"+ progress);
                        circle_progress.setProgress((float) progress);
                        circle_progress.setText(String.valueOf(progress) + "%");
                    }
                });
        downloadManager.add(downloadRequest);
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

    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, "Video Link Copied", Toast.LENGTH_SHORT).show();
    }

    private void sendtowhatsapp(String path) {
        try {
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            File fileWithinMyDir = new File(path);
            if (fileWithinMyDir.exists()) {
                intentShareFile.setType("image/*|video/*");
                intentShareFile.setPackage("com.whatsapp");

                intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", fileWithinMyDir));
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
               intentShareFile.putExtra(Intent.EXTRA_TEXT,"* No. 1 Short Videos App\n" +
                       getString(R.string.app_name)
                       +" Download, Support & Share\n" +
                       "Click on the link given below. * \uD83D\uDC49 https://play.google.com/store/apps/details?id="+context.getPackageName());
                startActivity(Intent.createChooser(intentShareFile, "Share File"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendtofacebook(String path) {

        try {

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            File fileWithinMyDir = new File(path);

            if (fileWithinMyDir.exists()) {
                intentShareFile.setType("image/*|video/*");
                intentShareFile.setPackage("com.facebook.katana");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", fileWithinMyDir));
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // intentShareFile.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id="+context.getPackageName());

                startActivity(Intent.createChooser(intentShareFile, "Share File"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendtoinstagram(String path) {

        try {

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            File fileWithinMyDir = new File(path);

            if (fileWithinMyDir.exists()) {
                intentShareFile.setType("image/*|video/*");
                intentShareFile.setPackage("com.instagram.android");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", fileWithinMyDir));
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // intentShareFile.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id="+context.getPackageName());
                startActivity(Intent.createChooser(intentShareFile, "Share File"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendtoother(String path) {

        try {

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            File fileWithinMyDir = new File(path);

            if (fileWithinMyDir.exists()) {
                intentShareFile.setType("image/*|video/*");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", fileWithinMyDir));
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // intentShareFile.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id="+context.getPackageName());
                startActivity(Intent.createChooser(intentShareFile, "Share File"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendtotwitter(String path) {

        try {

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            File fileWithinMyDir = new File(path);

            if (fileWithinMyDir.exists()) {
                intentShareFile.setType("image/*|video/*");
                intentShareFile.setPackage("com.twitter.android");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", fileWithinMyDir));
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //intentShareFile.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id="+context.getPackageName());
                startActivity(Intent.createChooser(intentShareFile, "Share File"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
                //.filter(new GlFilterGroup(new GlMonochromeFilter(), new GlVignetteFilter()))
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

        /*GlWatermarkFilter filter = new GlWatermarkFilter(myLogo, GlWatermarkFilter.Position.LEFT_TOP);
        new GPUMp4Composer(Environment.getExternalStorageDirectory() + "/Jagmee/" + videoId + "no_watermark" + ".mp4",
                Environment.getExternalStorageDirectory() + "/Jagmee/" + videoId + ".mp4")
                .filter(filter)
                .size(540, 960)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .listener(new GPUMp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        Log.e("resp onProgress", "" + (int) (progress * 100));
                        Functions.Show_loading_progress((int) ((progress * 100) / 2) + 50);
                    }

                    @Override
                    public void onCompleted() {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("resp onCompleted", "working");
                                Functions.cancel_determinent_loader();
                                Delete_file_no_watermark(videoId);
                                Scan_file(videoId);
                            }
                        });
                    }

                    @Override
                    public void onCanceled() {
                        Log.e("resp onCanceled", "onCanceled");
                    }

                    @Override
                    public void onFailed(Exception exception) {

                        Log.e("resp onFailed", exception.toString());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //Delete_file_no_watermark(videoId);
                                    Log.e("resp onFailed run", exception.toString());
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

                        if (action.contains("whatsapp")) {
                            sendtowhatsapp(yourFile.getAbsolutePath());
                        } else if(action.contains("facebook")) {
                            sendtofacebook(yourFile.getAbsolutePath());
                        } else if(action.contains("other")) {
                            sendtoother(yourFile.getAbsolutePath());
                        } else if(action.contains("instagram")) {
                            sendtoinstagram(yourFile.getAbsolutePath());
                        } else if(action.contains("twitter")) {
                            sendtotwitter(yourFile.getAbsolutePath());
                        }

                        dismiss();


                    }
                });
    }


    private String appendTwoVideos(String firstVideoPath, String secondVideoPath) {
        try {
            Movie[] inMovies = new Movie[2];

            inMovies[0] = MovieCreator.build(firstVideoPath);
            inMovies[1] = MovieCreator.build(secondVideoPath);

            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();

            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            }

            Movie result = new Movie();

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            BasicContainer out = (BasicContainer) new DefaultMp4Builder().build(result);

            @SuppressWarnings("resource")
            FileChannel fc = new RandomAccessFile(Environment.getExternalStorageDirectory() + "/wishbyvideo.mp4", "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/wishbyvideo.mp4";
        return mFileName;
    }


}
