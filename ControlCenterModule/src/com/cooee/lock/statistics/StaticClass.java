package com.cooee.lock.statistics;

import org.json.JSONException;
import org.json.JSONObject;

import com.cooee.shell.sdk.CooeeSdk;
import com.cooee.statistics.StatisticsExpandNew;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class StaticClass extends Service {
	private String appid = null;
	private String sn = null;
	private String launcherVersion = null;
	private String packagename = null;

	@Override
	public IBinder onBind(Intent intent) {
		Log.v("xxxx", "xxxxx");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		packagename = getPackageName();
		Assets.initAssets(this);
		JSONObject tmp = Assets.config;
		PackageManager mPackageManager = getPackageManager();
		try {
			JSONObject config = tmp.getJSONObject("config");
			appid = config.getString("app_id");
			sn = config.getString("serialno");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			launcherVersion = Integer.toString(mPackageManager.getPackageInfo(
					packagename, 0).versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		if (intent != null) {
			String eventType = intent.getStringExtra("EventType");
			Log.v("StaticClass action", eventType);
			if (intent != null && !eventType.equals("")) {
				StatisticsExpandNew.onEvent(getApplicationContext(), eventType, sn, appid, "", 1, getPackageName(), launcherVersion);
			} else {
				// @gaominghui2015/08/19 ADD START 添加push
				Log.i("StaticClass", "before init CooeeSdk!!");
				CooeeSdk.initCooeeSdk(this);
				// @gaominghui2015/08/19 ADD END
				Dbhelp dbhelp = new Dbhelp(getApplicationContext(),
						"lockbase.db");
				SQLiteDatabase sqliteDatabase = dbhelp.getWritableDatabase();
				Log.d("clear", "in first run sqliteDatabase = "
						+ sqliteDatabase);
				if (!dbhelp.onSerch(sqliteDatabase, "locktable")) {
					// dbhelp.onCreateTable(sqliteDatabase, "locktable");
					ContentValues values = new ContentValues();
					values.put("_id", 1);
					values.put("num", 1);
					sqliteDatabase.insert("locktable", null, values);
					Log.d("clear", "is first run");
					// xiatian add start //StatisticsNew
					StatisticsExpandNew.register(getApplicationContext(), sn,
							appid, "", 1, packagename, launcherVersion);
				} else {
					Log.d("clear", "is not first run");
					StatisticsExpandNew.use(getApplicationContext(), sn, appid,
							"", 1, packagename, launcherVersion);
				}
				sqliteDatabase.close();
			}
		}
		// stopSelf();
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
