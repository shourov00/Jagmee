package com.aliyun.svideo.downloader;

import android.database.sqlite.SQLiteDatabase;

public interface DbUpgradeListener {
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
