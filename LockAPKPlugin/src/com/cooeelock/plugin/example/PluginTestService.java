package com.cooeelock.plugin.example;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class PluginTestService extends Service {

	private final String TAG = "PluginTestService";
	private final String ACTION_JS_DATA = "com.cooeelock.core.plugin.jsdata";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.e(TAG, "######## onCreate  我是测试服务");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		Log.e(TAG, "######## onStartCommand  我是测试服务");

		Intent it = new Intent();
		it.setAction(ACTION_JS_DATA);
		it.putExtra("key_js_data", "javascript:onjsdata();");
		sendBroadcast(it);

		String authority = intent.getStringExtra("AUTHORITY");
		dataInsert(this,authority,"javascript:onjsdata();");
		
//		stopSelf();

		return super.onStartCommand(intent, flags, startId);
	}

	public void dataInsert(Context context,String authority,String str) {
		ContentResolver contentResolver = context
				.getContentResolver();
		Uri insertUri = Uri
				.parse("content://" + authority + ".plugin/apkplugin");
		ContentValues values = new ContentValues();
		values.put("data", str);
		contentResolver.insert(insertUri, values);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.e(TAG, "######## onDestroy  我是测试服务");
		super.onDestroy();
	}

}
