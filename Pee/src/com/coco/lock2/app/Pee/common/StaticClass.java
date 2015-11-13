package com.coco.lock2.app.Pee.common;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class StaticClass {

	public static final String LOCKBOX_PACKAGE_NAME = "com.coco.lock2.local.lockbox";
	public static final String ACTION_CHECK_ICON = "com.coco.lock.action.CHECK_ICON";

	
	public static boolean isLockBoxInstalled(Context cxt) {
		try {
			PackageInfo packageInfo = cxt.getPackageManager().getPackageInfo(
					StaticClass.LOCKBOX_PACKAGE_NAME, 0);
			Log.d("StaticClass", "packageInfo=" + packageInfo);
			return true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
}
