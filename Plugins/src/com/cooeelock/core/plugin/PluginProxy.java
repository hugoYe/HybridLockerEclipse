package com.cooeelock.core.plugin;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;

import com.cooeelock.plugin.example.PluginTest;

public class PluginProxy implements IPluginProxy {

	private final String TAG = "PluginProxy";

	/**
	 * 锁屏apk自身的上下文环境
	 * */
	private Context mRemoteContext;

	/**
	 * 插件本身的上下文环境
	 * */
	private Context mContext;

	/**
	 * @param context
	 *            锁屏自身的服务PluginProxyLoadService传过来的上下文环境
	 * */
	@Override
	public void init(Context context) {
		this.mRemoteContext = context;

		try {
			mContext = mRemoteContext.createPackageContext(
					"com.cooeelock.plugins", Context.CONTEXT_INCLUDE_CODE
							| Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int execute(String action, JSONArray args) {
		// TODO Auto-generated method stub
		Log.e(TAG, "####### action = " + action + ", args = " + args.toString());
		Log.e(TAG, "####### 我是插件真实执行者！！！ ");

		PluginTest.print(action, args);

		return -1;
	}

	@Override
	public void onPause(boolean multitasking) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onResume(boolean multitasking) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLauncherLoadFinish() {
		Log.d("web", "proxy onLauncherLoadFinish");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
	}

	@Override
	public Boolean shouldAllowRequest(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean shouldAllowNavigation(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean shouldOpenExternalUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onOverrideUrlLoading(String url) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Uri remapUri(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageFinishedLoading() {
		// TODO Auto-generated method stub
	}

}
