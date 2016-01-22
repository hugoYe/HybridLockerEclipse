package com.cooee.control.center.module.base;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("VivoXPlay", "BootCompletedReceiver");
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			String packageName = intent.getDataString().replaceAll("package:","");
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			SharedPreferences sharedPrefer = PreferenceManager
					.getDefaultSharedPreferences(context);
			int id = sharedPrefer.getInt(packageName, 0);
			mNotificationManager.cancel(id);
		}
	}
}
