package com.cooeelock.core.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.cooee.control.center.module.R;

public class DownloadService extends Service {

	private static int NOTIFICATIONID = 11;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		new DownloadTask(this, NOTIFICATIONID++, "Plugins", R.drawable.screen)
				.execute("http://www.coolauncher.cn/locker/Plugins.apk");
		return START_NOT_STICKY;
	}

}
