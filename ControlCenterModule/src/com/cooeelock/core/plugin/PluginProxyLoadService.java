package com.cooeelock.core.plugin;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cooee.control.center.module.base.FileUtils;

public class PluginProxyLoadService extends Service {

	private final String TAG = "PluginService";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.e(TAG, "######## onCreate !!!");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.e(TAG, "######## onStartCommand !!!");

		String destDir = getFilesDir().getAbsolutePath();
		try {
			FileUtils.copyAssetDirToFiles(destDir, this, "h5");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean rst = PluginProxyManager.getInstance().loadProxy(this);

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.e(TAG, "######## onDestroy !!!");
		super.onDestroy();
	}

}
