package com.cooeelock.plugin.example;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PluginTestService2 extends Service {

	private final String TAG = "PluginTestService2";

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

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.e(TAG, "######## onDestroy  我是测试服务");
		super.onDestroy();
	}

}
