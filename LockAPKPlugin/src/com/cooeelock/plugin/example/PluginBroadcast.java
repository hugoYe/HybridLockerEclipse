package com.cooeelock.plugin.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PluginBroadcast extends BroadcastReceiver {

	private final String TAG = "PluginBroadcast";

	public static final String ACTION_SYSTEM_DATA = "PluginBroadcast";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.e(TAG, "######## onReceive 我是系统进程广播接收者");

		Log.e(TAG, "######## onReceive intent.action = " + intent.getAction());
	}

}
