package com.cooee.statistics.databaseNew;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * 添加上传日志表
 * 
 */
public class JournalDBNew {

	public static final String KEY_ID = "_id";
	public static final String KEY_LOGTEXT = "logtext";
	public static final String KEY_UPLOADTIME = "uploadtime";
	private static final String DATABASE_TABLE = "logTable";
	private final Context context;
	private DatabaseHelperNew DBHelper;
	private SQLiteDatabase db;

	public JournalDBNew(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelperNew(context);
	}

	public static String getCreateSql() {
		String result = "create table logTable (_id integer primary key autoincrement, "
				+ "logtext text not null, uploadtime text);";
		return result;
	}

	public static String getDropSql() {
		String result = "DROP TABLE IF EXISTS " + DATABASE_TABLE;
		return result;
	}

	public JournalDBNew open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
		DBHelper.close();
	}

	public long insertTitle(String sLogText) {
		String uploadtime = getCurTime();
		return insertAllInfo(sLogText, uploadtime);
	}

	public long insertAllInfo(String sLogText, String uploadtime) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LOGTEXT, sLogText);
		initialValues.put(KEY_UPLOADTIME, uploadtime);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	public boolean deleteTitle(long sId) {
		return db.delete(DATABASE_TABLE, KEY_ID + "=" + sId, null) > 0;
	}

	public Cursor getAllTitles() {
		return db.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_LOGTEXT,
				KEY_UPLOADTIME }, null, null, null, null, null);
	}

	public boolean updateTitle(long sId, String sLogText) {
		String uploadtime = getCurTime();
		return updateAllInfo(sId, sLogText, uploadtime);
	}

	public boolean updateAllInfo(long sId, String sLogText, String uploadtime) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LOGTEXT, sLogText);
		initialValues.put(KEY_UPLOADTIME, uploadtime);
		return db.update(DATABASE_TABLE, initialValues, KEY_ID + "=" + sId,
				null) > 0;
	}

	private String getCurTime() {
		String time = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());
		time = formatter.format(curDate);
		return time;
	}
}
