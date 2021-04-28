package com.aliyun.svideo.recorder.util;

import android.content.Context;
import android.util.Log;
import com.aliyun.svideo.downloader.DownloaderManager;
import com.aliyun.svideo.downloader.FileDownloaderModel;
import com.aliyun.svideo.base.http.EffectService;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 录制assets资源解压
 */
public class RecordCommon {

    private static final String TAG = "RecordCommon";
    private static String SD_DIR ;
    public final static String QU_NAME = "AliyunDemo";
    public final static String RACE_NAME = "race_res";
    private final static String QU_COLOR_FILTER = "filter";
    private static final String QU_ANIMATION_FILTER = "animation_filter";
    private final static String QU_PASTER = "maohuzi";
    private static String QU_DIR ;
    private static String RACE_DIR ;

    private static void copySelf(Context cxt, String root) {
        try {
            String[] files = cxt.getAssets().list(root);
            if (files.length > 0) {
                File subdir = new File(RecordCommon.SD_DIR + root);
                if (!subdir.exists()) {
                    subdir.mkdirs();
                }
                for (String fileName : files) {
                    if (new File(RecordCommon.SD_DIR + root + File.separator + fileName).exists()) {
                        continue;
                    }
                    copySelf(cxt, root + "/" + fileName);
                }
            } else {
                Log.d(TAG, "copy...." + RecordCommon.SD_DIR + root);
                OutputStream myOutput = new FileOutputStream(RecordCommon.SD_DIR + root);
                InputStream myInput = cxt.getAssets().open(root);
                byte[] buffer = new byte[1024 * 8];
                int length = myInput.read(buffer);
                while (length > 0) {
                    myOutput.write(buffer, 0, length);
                    length = myInput.read(buffer);
                }

                myOutput.flush();
                myInput.close();
                myOutput.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public void copyAll(Context cxt) {
        SD_DIR = getExtFileDir(cxt);
        QU_DIR = SD_DIR + QU_NAME + File.separator;
        File dir = new File(RecordCommon.QU_DIR);
        copySelf(cxt, QU_NAME);
        dir.mkdirs();
        unZip(RecordCommon.SD_DIR + QU_NAME);
    }

    /**
     * @param cxt 拷贝race相关资源到沙盒目录内
     */
    static public void copyRace(Context cxt) {

        SD_DIR = getExtFileDir(cxt);
        RACE_DIR = SD_DIR + RACE_NAME + File.separator;
        File dir = new File(RecordCommon.RACE_DIR);
        copySelf(cxt, RACE_NAME);
        dir.mkdirs();
    }

    private static String getExtFileDir(Context cxt) {
        return cxt.getExternalFilesDir("") + File.separator;
    }

    public static void unZip(String srcDir) {
        File[] files = new File(srcDir).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name != null && name.endsWith(".zip")) {
                    return true;
                }
                return false;
            }
        });
        if (files == null) {
            return;
        }
        for (final File file : files) {
            int len = file.getAbsolutePath().length();
            if (!new File(file.getAbsolutePath().substring(0, len - 4)).exists()) {
                try {
                    unZipFolder(file.getAbsolutePath(), srcDir);
                    insertDB(file.getAbsolutePath().substring(0, len - 4));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void unZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {

                File file = new File(outPathString + File.separator + szName);
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }

    private static void insertDB(String name) {
        if (name.endsWith(QU_PASTER)) {
            insertPaster();
        }
    }


    private static void insertPaster() {
        File file = new File(QU_DIR, QU_PASTER);
        if (file.exists() && file.isDirectory()) {
            FileDownloaderModel model = new FileDownloaderModel();
            model.setId(150);
            model.setPath(QU_DIR + QU_PASTER );
            //        if(icon == null || "".equals(icon)) {
            String icon = model.getPath() + "/icon.png";
            //        }
            model.setIcon(icon);
            model.setName("maohuzi");
            model.setIsunzip(0);
            model.setEffectType(EffectService.EFFECT_FACE_PASTER);
            model.setTaskId(FileDownloadUtils.generateId(String.valueOf(model.getId()), model.getPath()));

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(FileDownloaderModel.SUBID, String.valueOf(model.getId()));
            hashMap.put(FileDownloaderModel.TASK_ID, String.valueOf(model.getTaskId()));
            DownloaderManager.getInstance().getDbController().insertDb(model, hashMap);
        }
    }

    /**
     * 获取滤镜
     * @return List<String>
     */
    public static List<String> getColorFilterList() {
        List<String> list = new ArrayList<>();
        File file = new File(QU_DIR, QU_COLOR_FILTER);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileTemp : files) {
                if (fileTemp.exists()) {
                    list.add(fileTemp.getAbsolutePath());
                }
            }
        }
        return list;
    }

    /**
     * 获取动效滤镜
     * @return List<String>
     */
    public static List<String> getAnimationFilterList() {
        List<String> list = new ArrayList<>();
        File file = new File(QU_DIR, QU_ANIMATION_FILTER);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileTemp : files) {
                if (fileTemp.exists()) {
                    list.add(fileTemp.getAbsolutePath());
                }
            }
        }
        return list;
    }
}
