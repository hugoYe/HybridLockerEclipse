package com.cooeeui.core.plugin;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class PluginProvider extends ContentProvider {

	private DBHelper dbHelper = null;
	// MIME类型
	static final String PERSIONS_TYPE_JAR = "vnd.android.cursor.dir/jarplugin";
	static final String PERSION_ITEM_TYPE_JAR = "vnd.android.cursor.item/jarplugin";
	static final String PERSIONS_TYPE_APK = "vnd.android.cursor.dir/apkplugin";
	static final String PERSION_ITEM_TYPE_APK = "vnd.android.cursor.item/apkplugin";
	// 返回码
	static final int CODES = 2;
	static final int CODE = 1;

	public static String AUTHORITY;
	static UriMatcher uriMatcher; // Uri匹配
	public static Uri PLUGIN_CONTENT_URI_JAR;
	public static Uri PLUGIN_CONTENT_URI_APK;

	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		AUTHORITY = getContext().getPackageName() + ".plugin";
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "jarplugin", CODES);
		uriMatcher.addURI(AUTHORITY, "jarplugin/#", CODE);
		uriMatcher.addURI(AUTHORITY, "apkplugin", CODES);
		uriMatcher.addURI(AUTHORITY, "apkplugin/#", CODE);
		PLUGIN_CONTENT_URI_JAR = Uri.parse("content://" + AUTHORITY + "/jarplugin");
		PLUGIN_CONTENT_URI_APK = Uri.parse("content://" + AUTHORITY + "/apkplugin");
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = null;
		String table = "";
		Log.v("###********######", "uri =  "+uri.toString());
		if (uri.toString().contains("jarplugin")) {
			table = DBHelper.TABLE_JAR;
		} else if (uri.toString().contains("apkplugin")) {
			table = DBHelper.TABLE_APK;
		}
		Log.v("###********######", "table =  "+table);
		switch (uriMatcher.match(uri)) {
		case CODES:
			Log.v("###********######", "CODES =  "+CODES);
			cursor = db.query(table, projection, selection, selectionArgs,
					null, null, sortOrder);
			Log.v("###********######", "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			break;
		case CODE:
			long id = ContentUris.parseId(uri);
			String where = "id=" + id;
			where += !TextUtils.isEmpty(selection) ? " and (" + selection + ")"
					: "";
			cursor = db.query(table, projection, where, selectionArgs, null,
					null, sortOrder);
			break;
		default:
			break;
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		String presionsType = "";
		String presionsItemType = "";
		if (uri.toString().contains("jarplugin")) {
			presionsType = PERSIONS_TYPE_JAR;
			presionsItemType = PERSION_ITEM_TYPE_JAR;
		} else if (uri.toString().contains("apkplugin")) {
			presionsType = PERSIONS_TYPE_APK;
			presionsItemType = PERSION_ITEM_TYPE_APK;
		}
		switch (uriMatcher.match(uri)) {
		case CODES:
			return presionsType; // 这里CODES代表集合，故返回的是集合类型的MIME
		case CODE:
			return presionsItemType;
		default:
			throw new IllegalArgumentException("throw Uri:" + uri.toString());
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = 0;
		String table = "";
		if (uri.toString().contains("jarplugin")) {
			table = DBHelper.TABLE_JAR;
		} else if (uri.toString().contains("apkplugin")) {
			table = DBHelper.TABLE_APK;
		}
		switch (uriMatcher.match(uri)) {
		case CODES:
			id = db.insert(table, DBHelper.TABLE_COLUMN_LABEL, values);
			return ContentUris.withAppendedId(uri, id);
		case CODE:
			id = db.insert(table, DBHelper.TABLE_COLUMN_LABEL, values);
			String path = uri.toString();
			return Uri.parse(path.substring(0, path.lastIndexOf("/")) + id);
		default:
			throw new IllegalArgumentException("throw Uri:" + uri.toString());
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		String table = "";
		if (uri.toString().contains("jarplugin")) {
			table = DBHelper.TABLE_JAR;
		} else if (uri.toString().contains("apkplugin")) {
			table = DBHelper.TABLE_APK;
		}
		switch (uriMatcher.match(uri)) {
		case CODES:
			count = db.delete(table, selection, selectionArgs);
			break;
		case CODE:
			long id = ContentUris.parseId(uri);
			String where = "id=" + id;
			where += !TextUtils.isEmpty(selection) ? " and (" + selection + ")"
					: "";
			count = db.delete(table, where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("throw Uri:" + uri.toString());
		}
		db.close();
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
