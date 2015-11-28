package com.cooeelock.core;

import org.json.JSONArray;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;

import com.cooeelock.plugin.example.DownloadTask;

public class PluginProxy implements IPluginProxy {

	private final String TAG = "PluginProxy";

	private final String ACTION_EXAMPLE = "example";

	/**
	 * 锁屏apk的上下文环境
	 * */
	private Context mRemoteContext;

	/**
	 * @param remoteContext
	 *            锁屏apk的上下文环境
	 * */
	public PluginProxy(Context remoteContext) {
		this.mRemoteContext = remoteContext;
	}

	private static int NOTIFICATIONID = 11;

	@Override
	public int execute(String action, final JSONArray args) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### 我是插件真实执行者！！！ action =  " + action);

		if (action.equals(ACTION_EXAMPLE)) {
			Log.i(TAG, "####### 我是插件真实执行者！！！  开始下载  ");
			new DownloadTask(mRemoteContext, NOTIFICATIONID++, "Plugins",
					R.drawable.ic_dialog_alert)
					.execute("http://www.coolauncher.cn/locker/Plugins.apk");
		}
		Log.i(TAG, "####### 我是插件真实执行者！！！done");
		return -1;
	}

	@Override
	public void onPause(Boolean multitasking) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onPause " + multitasking.booleanValue());
	}

	@Override
	public void onResume(Boolean multitasking) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onResume " + multitasking.booleanValue());
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onStart ");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onStop ");
	}

	@Override
	public void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onNewIntent intent = " + intent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onDestroy ");
	}

	@Override
	public void onLauncherLoadFinish() {
		Log.i(TAG, "####### onLauncherLoadFinish ");
	}

	@Override
	public void onActivityResult(Integer requestCode, Integer resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG,
				"####### onActivityResult requestCode = "
						+ requestCode.intValue() + ", resultCode = "
						+ resultCode.intValue() + ", intent = " + intent);
	}

	@Override
	public Boolean shouldAllowRequest(String url) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### shouldAllowRequest url = " + url);
		return null;
	}

	@Override
	public Boolean shouldAllowNavigation(String url) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### shouldAllowNavigation url = " + url);
		return null;
	}

	@Override
	public Boolean shouldOpenExternalUrl(String url) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### shouldOpenExternalUrl url = " + url);
		return null;
	}

	@Override
	public boolean onOverrideUrlLoading(String url) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onOverrideUrlLoading url = " + url);
		return false;
	}

	@Override
	public Uri remapUri(Uri uri) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### remapUri uri = " + uri);
		return null;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageFinishedLoading() {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onPageFinishedLoading ");
	}

}
