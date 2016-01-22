package com.coco.lock2.app.info;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.provider.CallLog.Calls;

public class CallNotify {
	String date = "";
	String number = "";
	String out = "";

	public CallNotify() {

	}

	public int getMissedCallCount(Context context) {
		int missedCallCount = 0;
		Cursor callCursor = null;
		try {
			callCursor = context.getContentResolver().query(
					Calls.CONTENT_URI,
					new String[] { Calls.CACHED_NAME, Calls.NUMBER, Calls.DATE,
							Calls.TYPE, Calls.NEW }, "type=3 and new=1", null,
					Calls.DATE);
			if (callCursor != null) {
				missedCallCount = callCursor.getCount();
			}
		} finally {
			if (callCursor != null) {
				callCursor.close();
			}
		}
		return missedCallCount;
	}

	public void registerObserver(Context context, ContentObserver observer) {
		context.getContentResolver().registerContentObserver(Calls.CONTENT_URI,
				true, observer);
	}

	public void unregisterObserver(Context context, ContentObserver observer) {
		context.getContentResolver().unregisterContentObserver(observer);
	}
}
