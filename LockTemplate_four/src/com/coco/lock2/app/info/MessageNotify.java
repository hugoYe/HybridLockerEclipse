package com.coco.lock2.app.info;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

public class MessageNotify {

	private Uri smsUri = Uri.parse("content://sms");
	private Uri mmsUri = Uri.parse("content://mms/inbox");

	public MessageNotify() {

	}

	public int getUnreadSMS(Context context) {
		int smsCount = 0;
		Cursor csr = null;
		try {
			csr = context.getContentResolver().query(smsUri, null,
					"type = 1 and read = 0", null, null);
			if (csr != null) {
				smsCount = csr.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (csr != null) {
				csr.close();
			}
		}
		return smsCount;
	}

	public int getUnreadMMS(Context context) {
		int smsCount = 0;
		Cursor csr = null;
		try {
			csr = context.getContentResolver().query(mmsUri, null, "read = 0",
					null, null);
			if (csr != null) {
				smsCount = csr.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (csr != null) {
				csr.close();
			}
		}
		return smsCount;
	}

	public int getUnreadCount(Context context) {
		return getUnreadSMS(context) + getUnreadMMS(context);
	}

	private void registerSMSObserver(Context context, ContentObserver observer) {
		context.getContentResolver().registerContentObserver(smsUri, true,
				observer);
	}

	private void registerMMSObserver(Context context, ContentObserver observer) {
		context.getContentResolver().registerContentObserver(mmsUri, true,
				observer);
	}

	public void registerObserver(Context context, ContentObserver observer) {
		registerSMSObserver(context, observer);
		registerMMSObserver(context, observer);
	}

	public void unregisterObserver(Context context, ContentObserver observer) {
		context.getContentResolver().unregisterContentObserver(observer);
	}
}
