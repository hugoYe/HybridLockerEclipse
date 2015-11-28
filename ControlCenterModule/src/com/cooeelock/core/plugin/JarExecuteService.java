package com.cooeelock.core.plugin;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cooee.control.center.module.base.FileUtils;

public class JarExecuteService extends Service {

	private final String TAG = "JarExecuteService";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		File file = getFilesDir();
		Log.e(TAG, "######## onCreate file = " + file);
		if (file != null) {
			String destDir = getFilesDir().getAbsolutePath();
			Log.e(TAG, "######## onCreate destDir = " + destDir);
			try {
				FileUtils.copyAssetDirToFiles(destDir, this, "h5");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.e(TAG, "######## onStartCommand");
		boolean loadRst = JarPluginProxyManager.getInstance().loadProxy(this);
		if (!loadRst) {
			Log.e(TAG, "######## JarPluginProxyManager load failed ! ");
		}
		String action = intent.getStringExtra("key_action");
		String jsonArray = intent.getStringExtra("key_jsonarray");
		Log.e(TAG, "######## action = " + action + ", jsonArray = " + jsonArray);
		JSONArray args = null;
		try {
			args = new JSONArray(jsonArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JarPluginProxyManager.getInstance().execute(action, args);
		return START_NOT_STICKY;
	}
}
