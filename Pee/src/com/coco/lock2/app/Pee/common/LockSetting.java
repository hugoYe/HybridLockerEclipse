package com.coco.lock2.app.Pee.common;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class LockSetting {

	private static final String LOG_TAG = "LockSetting";

	public static final String FIELD_TIMESTYLE = "timestyle";
	public static final String FIELD_SOUND = "sound";
	public static final String FIELD_VIBRATE = "vibrate";
	public static final Uri CONTENT_URI = Uri
			.parse("content://com.coco.lock2.local.lockbox/config");

	private int timeStyle = 0;
	private boolean useSound = false;
	private boolean useVibrate = false;

	public boolean isSoundOpen() {
		return useSound;
	}

	public boolean isVibrateOpen() {
		return useVibrate;
	}

	public boolean is24HourFormat() {
		return timeStyle == 0;
	}

	public boolean loadSetting(Context context) {

		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
				null);

		if (cursor == null || !cursor.moveToFirst()) {
			Log.d(LOG_TAG, "cursor failed");
			return false;
		}

		for (int i = 0; i < cursor.getColumnCount(); i++) {
			String columnName = cursor.getColumnName(i);
			if (FIELD_TIMESTYLE.equals(columnName)) {
				timeStyle = cursor.getInt(i);
			} else if (FIELD_SOUND.equals(columnName)) {
				useSound = cursor.getInt(i) != 0;
			} else if (FIELD_VIBRATE.equals(columnName)) {
				useVibrate = cursor.getInt(i) != 0;
			}
		}

		cursor.close();
		return true;
	}
}
