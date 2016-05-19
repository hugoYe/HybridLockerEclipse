package com.cooeeui.core.plugin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "plugin.db";
    static final int DB_VERSION = 1;
    static final String TABLE_JAR = "jarplugin";
    static final String TABLE_APK = "apkplugin";
    static final String TABLE_COLUMN_DATA = "data";
    static final String TABLE_COLUMN_LABEL = "label";
    static final String
        CREATE_TABLE_JAR =
        "create table jarplugin(id integer primary key autoincrement,label varchar,data varchar)";
    static final String
        CREATE_TABLE_APK =
        "create table apkplugin(id integer primary key autoincrement,label varchar,data varchar)";
    static final String DRPO_TABLE_JAR = "drop table if exists jarplugin";
    static final String DRPO_TABLE_APK = "drop table if exists apkplugin";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_JAR);
        db.execSQL(CREATE_TABLE_APK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 这里其实是比较版本，然后升级数据库的，比如说是增加一个字段，或者删除一个字段，或者增加表
        db.execSQL(DRPO_TABLE_JAR);
        db.execSQL(DRPO_TABLE_APK);
        onCreate(db);
    }

}
