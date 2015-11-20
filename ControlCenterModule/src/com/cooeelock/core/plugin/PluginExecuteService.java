package com.cooeelock.core.plugin;

import org.json.JSONArray;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PluginExecuteService extends Service {

	private final String TAG = "PluginExecuteService";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.e(TAG, "######## onStartCommand !!!");

		String action = intent.getStringExtra("action");

		Log.e(TAG, "######## action = " + action);

		final int handle = PluginProxyManager.getInstance().execute(action,
				new JSONArray());

		return START_NOT_STICKY;
	}

}
