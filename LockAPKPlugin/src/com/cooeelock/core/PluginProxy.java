package com.cooeelock.core;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.cooeelock.plugin.example.ExamplePlugin;
import com.kmob.kmobsdk.AdBaseView;
import com.kmob.kmobsdk.AdViewListener;
import com.kmob.kmobsdk.KmobManager;

public class PluginProxy implements IPluginProxy {

	private final String TAG = "PluginProxy";

	private final String ACTION_EXAMPLE = "example";

	/**
	 * 系统锁屏apk的上下文环境
	 * */
	private Context mRemoteContext;

	/**
	 * 插件本身的上下文环境
	 * */
	private Context mContext;

	/**
	 * @param remoteContext
	 *            系统锁屏apk的上下文环境(4.4、5.0为systemui进程的)
	 * @param context
	 *            插件apk本身的上下文环境,context = mRemoteContext.createPackageContext(
	 *            "com.cooeeui.lock.plugins", Context.CONTEXT_INCLUDE_CODE |
	 *            Context.CONTEXT_IGNORE_SECURITY);
	 * */
	public PluginProxy(Context remoteContext, Context context) {
		this.mRemoteContext = remoteContext;
		this.mContext = context;
	}

	@Override
	public int execute(String action, final JSONArray args) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### 我是插件真实执行者！！！ action =  " + action);

		// 1.获取实时的广告View，传入，apk唯一id，广告位id，广告类型
		AdBaseView mrsplashView = KmobManager.createRsplash(
				"20151012061048241", mRemoteContext);
		mrsplashView.addAdViewListener(new AdViewListener() {

			@Override
			public void onAdShow(String info) {
				Log.e(TAG, "######## AdViewListener onAdShow info " + info);
			}

			@Override
			public void onAdReady(String space_id) {
				Log.e(TAG, "######## AdViewListener onAdReady space_id "
						+ space_id);
			}

			@Override
			public void onAdFailed(String reason) {
				Toast.makeText(mRemoteContext, "获取广告失败", Toast.LENGTH_LONG)
						.show();
				Log.e(TAG, "######## AdViewListener onAdFailed reason "
						+ reason);
			}

			@Override
			public void onAdClick(String arg0) {
				Log.e(TAG, "######## AdViewListener onAdClick arg0 " + arg0);
			}

			@Override
			public void onAdClose(String info) {
			}

			@Override
			public void onAdCancel(String info) {
			}
		});

		String exectype = "none";
		int version = -1;
		try {
			exectype = args.getString(1);
			version = args.getInt(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.i(TAG, "#######  action = " + action + ", exectype = " + exectype
				+ ", version = " + version);

		Intent it = new Intent();
		it.setClassName(mContext.getPackageName(),
				"com.cooeelock.plugin.example.PluginTestService");
		mContext.startService(it);

		if (action.equals(ACTION_EXAMPLE)) {
			return handleExample(args);
		}

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

	private int handleExample(JSONArray args) {
		Log.i(TAG, "######## 我是示例接口  ExamplePlugin ！！！");
		ExamplePlugin.execute(args);
		return -1;
	}

}
