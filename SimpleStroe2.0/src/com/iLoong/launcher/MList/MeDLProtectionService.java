package com.iLoong.launcher.MList;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MeDLProtectionService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		Log.v("ME_RTFSC", "------------- MeDLProtectionService::onCreate");
		new Thread(new Runnable() {

			public void run() {
				while (true) {
					MELOG.v("ME_RTFSC", "MeDLProtectionService  sleep");
					try {
						Thread.sleep(3000L);
					} catch (Exception localException) {
					}
				}
			}
		}).start();

		super.onCreate();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("ME_RTFSC",
				"-------------- MeDLProtectionService::onStartCommand");
		if (android.os.Build.VERSION.SDK_INT < 18) {
			Notification localNotification = new Notification(0, "ME_DL_Start",
					System.currentTimeMillis());
			localNotification.setLatestEventInfo(this, "CooeeLauncher ",
					"CooeeLauncher Start", PendingIntent.getActivity(this, 0,
							new Intent(this, MEServiceActivity.class), 0));
			startForeground(10311655, localNotification);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		Log.v("ME_RTFSC", "------------- MeDLProtectionService::onDestroy");
		if (android.os.Build.VERSION.SDK_INT < 18) {
			stopForeground(true);
		}

		super.onDestroy();
	}
}
