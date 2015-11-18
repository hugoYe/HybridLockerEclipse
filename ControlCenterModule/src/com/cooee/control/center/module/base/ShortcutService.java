package com.cooee.control.center.module.base;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;

public class ShortcutService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		createSystemShortCut(this, (Intent)intent.getParcelableExtra("app_intent"),
				intent.getStringExtra("app_title"),
				(Bitmap)intent.getParcelableExtra("app_icon"));
		stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 创建桌面快捷方式图标
	 * 
	 * @author hugo.ye
	 * @param context
	 * @param intent
	 * @param title
	 * @param icon
	 * */
	private void createSystemShortCut(Context context, Intent intent,
			String title, Bitmap icon) {
		final Intent addIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		addIntent.putExtra("duplicate", false);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);// 快捷方式的标题
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);// 快捷方式的图标
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);// 快捷方式的动作
		context.sendBroadcast(addIntent);
	}
}
