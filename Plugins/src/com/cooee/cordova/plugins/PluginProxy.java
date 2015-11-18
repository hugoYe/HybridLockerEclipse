package com.cooee.cordova.plugins;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;

import com.cooee.control.center.module.base.PluginTest;

public class PluginProxy implements IPluginProxy {

	private static final String TAG = "PluginProxy";

	private Context mContext;
	private WebView view;

	@Override
	public void init(Context context, WebView view) {
		this.mContext = context;
		this.view = view;
	}

	@Override
	public int execute(Intent intent, String title, Bitmap icon) {
		// TODO Auto-generated method stub
		Log.e(TAG, "####### execute !!!!!!");

		PluginTest test = new PluginTest();
		test.print(mContext.getPackageName(), title, intent);

		Context myContext = null;
		try {
			myContext = mContext.createPackageContext("com.cooee.plugins",
					Context.CONTEXT_INCLUDE_CODE
							| Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent2 = new Intent();
		intent2.putExtra("app_title", title);
		intent2.putExtra("app_icon", icon);
		intent2.putExtra("app_intent", intent);
		intent2.setClassName(myContext.getPackageName(),
				"com.cooee.control.center.module.base.ShortcutService");
		myContext.startService(intent2);
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
