package com.cooee.control.center.module.update;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class UpdateService extends Service {
	static final String ACTION_DOWN_FINISH = "com.coco.lock.app.down_finish";
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_DOWN_FINISH)) {
				stopSelf();
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		new UpdateTask(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_DOWN_FINISH);
		registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mBroadcastReceiver != null) {
			unregisterReceiver(mBroadcastReceiver);
			mBroadcastReceiver = null;
		}
	}
}
