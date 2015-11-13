package com.cooee.statistics.databaseNew;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * 添加配置表
 * 
 */
public class ConfigDBNew {

	public static final String KEY_NAME = "name";
	public static final String KEY_VALUE = "value";
	private static final String DATABASE_TABLE = "configtable";
	private static String DEFAULT_ERRTIME = "YYYYMMDDHHMMSS";
	private static final String ERRTIME = "ErrorTime";
	private static final String ERRCOUNT = "ErrorCount";
	private final Context context;
	private DatabaseHelperNew DBHelper;
	private SQLiteDatabase db;

	public ConfigDBNew(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelperNew(context);
	}

	public static String getCreateSql() {
		String result = "create table configtable (name TEXT primary key not null, "
				+ "value text);";
		return result;
	}

	public static String getDropSql() {
		String result = "DROP TABLE IF EXISTS " + DATABASE_TABLE;
		return result;
	}

	public void initconfdb() {
		open();
		insertTitle(ERRTIME, DEFAULT_ERRTIME);
		insertTitle(ERRCOUNT, "0");
		close();
	}

	public ConfigDBNew open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
		DBHelper.close();
	}

	public long insertTitle(String sName, String sValue) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, sName);
		initialValues.put(KEY_VALUE, sValue);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	public boolean deleteTitle(String sName) {
		return db.delete(DATABASE_TABLE, KEY_NAME + "=" + sName, null) > 0;
	}

	public Cursor getAllTitles() {
		return db.query(DATABASE_TABLE, new String[] { KEY_NAME, KEY_VALUE },
				null, null, null, null, null);
	}

	private SQLiteDatabase getDatabase() {
		return DBHelper.getWritableDatabase();
	}

	public String getTitle(String sName) throws SQLException {
		String result = "";
		SQLiteDatabase db = getDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(DATABASE_TABLE, null, KEY_NAME + "=?",
					new String[] { sName }, null, null, null);
			if (cursor.moveToFirst()) {
				result = cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_VALUE));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}

	public boolean updateTitle(String sName, String sValue) {
		ContentValues args = new ContentValues();
		args.put(KEY_VALUE, sValue);
		// return db.update(DATABASE_TABLE, args, KEY_NAME + "=" + sName, null)
		// > 0;
		return db.update(DATABASE_TABLE, args, KEY_NAME + "=?",
				new String[] { sName }) > 0;
	}
}
