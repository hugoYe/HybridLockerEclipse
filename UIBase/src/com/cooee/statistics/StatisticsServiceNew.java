package com.cooee.statistics;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class StatisticsServiceNew extends Service {

	public final static String EXTRA_EVENT_ID = "EVENT_ID";
	public final static String EXTRA_PARAMS = "PARAMS";

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_NOT_STICKY;
	}

	private void handleCommand(Intent intent) {
		StatisticsBaseNew.packageSetAppContext(this.getApplicationContext());

		String eventId = intent.getStringExtra(EXTRA_EVENT_ID);
		if (eventId == null) {
			eventId = "";
		}
		String params = intent.getStringExtra(EXTRA_PARAMS);
		if (params == null) {
			params = "";
		}

		StatisticsBaseNew.packageOnEvent(this, eventId, params);
		stopSelf();
	}
}
