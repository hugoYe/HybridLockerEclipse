package com.cooeelock.core.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.R;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.cooee.cordova.plugins.UnlockListener;

public class ApkPlugin extends CordovaPlugin {

	private final String TAG = "ApkPlugin";

	private final String ACTION_DOWNLOAD_APK = "downLoadApk";
	private final String ACTION_UNLOCK = "unlock";
	private final int EXECUTE_RESULT_NOT_HANDLE = -1;
	private final int EXECUTE_RESULT_HANDLE_FAIL = 0;
	private final int EXECUTE_RESULT_HANDLE_SUCCESS = 1;

	private final String ACTION_JS_DATA = "com.cooeelock.core.apkplugin.jsdata";

	private Context mContext;

	private int mHandleRst;
	private CallbackContext mCallbackContext;

	private static UnlockListener sUnlockListener;

	private BroadcastReceiver mJsdataReceiver;

	class JsdataBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.e(TAG, "######## action = " + action);
			if (action.equals(ACTION_JS_DATA)) {
				Log.e(TAG, "########  " + intent.getStringExtra("key_js_data"));
				sendJS(intent.getStringExtra("key_js_data"));
				deleteJsData(context);
			}

			boolean unlock = intent.getBooleanExtra("key_unlock", false);
			if (unlock) {
				if (sUnlockListener != null) {
					sUnlockListener.onUnlock();
				}
			}
		}
	}

	public static void setOnUnlockListener(UnlockListener unlockListener) {
		sUnlockListener = unlockListener;
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		// TODO Auto-generated method stub
		super.initialize(cordova, webView);
		if (cordova.getActivity() != null) {
			mContext = cordova.getActivity();
		} else if (cordova.getContext() != null) {
			mContext = cordova.getContext();
		}

		mJsdataReceiver = new JsdataBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_JS_DATA);
		mContext.registerReceiver(mJsdataReceiver, filter);

		Log.v("###********######", getJsData(mContext));
	}

	private void deleteJsData(Context context) {
		ContentResolver resolver = context.getContentResolver();
		Uri contentUri = Uri.parse("content://"+context.getPackageName()+".plugin/apkplugin");
		resolver.delete(contentUri, null, null);
	}
	
	private String getJsData(Context context) {
		String data = "";
		Cursor csr = null;
		try {
			ContentResolver resolver = context.getContentResolver();
			Uri contentUri = Uri.parse("content://"+context.getPackageName()+".plugin/apkplugin");
			Log.v("###********######", "contentUri"+contentUri.toString());
			csr = resolver.query(contentUri, null, null, null, null);
			Log.v("###********######", "csr = "+csr.getCount());
			if (csr != null) {
				while (csr.moveToNext()) {
					data = csr.getString(csr.getColumnIndexOrThrow("data"));
				}
			}
		} catch (Exception e) {
			Log.v("###********######", "Exception!!!!!!!!!!");
			e.printStackTrace();
		} finally {
			if (csr != null) {
				csr.close();
			}
		}
		return data;
	}

	@Override
	public boolean execute(final String action, final JSONArray args,
			final CallbackContext callbackContext) throws JSONException {

		mCallbackContext = callbackContext;

		Log.e(TAG, "########  " + args.toString());
		Log.v("###********######", getJsData(mContext));
		
		if (action.equals(ACTION_UNLOCK)) {
			if (sUnlockListener != null) {
				sUnlockListener.onUnlock();
			}
			return true;
		}
		
		if (action.equals(ACTION_DOWNLOAD_APK)) {
			startDownApkService(mContext, args);
			return true;
		}

		int version = args.getInt(0);

		if (!isPluginAPKInstalled(mContext,
				ApkPluginProxyManager.PLUGINS_PACKAGE_NAME)) {
			// 下载apk
			Log.i(TAG, "######## 需要下载apk !!!!!!");
			sendJS("javascript:showTip();");
			return false;
		}

		if (isPluginApkNeedUpdate(version)) {
			// 更新apk
			Log.i(TAG, "######## 更新apk !!!!!!");
			startDownApkService(mContext, args);
			return false;
		}
		String jsData = getJsData(mContext);
		if (jsData != null && !jsData.equals("")) {
			sendJS(getJsData(mContext));
		}
		boolean loadRst = false;
		if (cordova.getActivity() != null) {
			loadRst = ApkPluginProxyManager.getInstance().loadProxy(
					cordova.getActivity());
		} else if (cordova.getRemoteContext() != null) {
			loadRst = ApkPluginProxyManager.getInstance().loadProxy(
					cordova.getRemoteContext());
		}
		if (!loadRst) {
			mCallbackContext.error("CooeelockPlugin load failed !");
			return false;
		}

		final String execThreadType = args.getString(1);
		Log.i(TAG, "######## execThreadType = " + execThreadType);
		if (execThreadType.equals("EXEC_TYPE_UI")) {
			if (cordova.getActivity() != null) {
				cordova.getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						handleExecute(action, args);
					}
				});
			} else if (cordova.getCordovaWrap() != null) {
				cordova.getCordovaWrap().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						handleExecute(action, args);
					}
				});
			}
			return true;
		} else if (execThreadType.equals("EXEC_TYPE_THREAD_POOL")) {
			cordova.getThreadPool().execute(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					handleExecute(action, args);
				}
			});
			return true;
		} else {
			handleExecute(action, args);
			
			// test
			// Intent intent = null;
			// try {
			// intent = Intent.parseUri(args.getString(0), 0);
			// } catch (URISyntaxException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// final int handle =
			// PluginProxyManager.getInstance().execute(action,
			// args);
			// PluginProxyManager.getInstance().getInstance()
			// .onPause(Boolean.FALSE);
			// PluginProxyManager.getInstance().getInstance()
			// .onResume(Boolean.TRUE);
			// PluginProxyManager.getInstance().getInstance().onStart();
			// PluginProxyManager.getInstance().getInstance().onStop();
			// PluginProxyManager.getInstance().getInstance().onNewIntent(intent);
			// PluginProxyManager.getInstance().getInstance().onDestroy();
			// PluginProxyManager.getInstance().getInstance()
			// .onLauncherLoadFinish();
			//
			// PluginProxyManager.getInstance().getInstance()
			// .onActivityResult(1, 2, intent);
			// PluginProxyManager.getInstance().getInstance()
			// .shouldAllowRequest("url sdfasf");
			// PluginProxyManager.getInstance().getInstance()
			// .shouldAllowNavigation("url sdfasf");
			// PluginProxyManager.getInstance().getInstance()
			// .shouldOpenExternalUrl("url sdfasf");
			// PluginProxyManager.getInstance().getInstance()
			// .onOverrideUrlLoading("url sdfasf");
			// PluginProxyManager.getInstance().getInstance()
			// .remapUri(Uri.parse("uri rui"));
			// PluginProxyManager.getInstance().getInstance()
			// .onPageFinishedLoading();
			return true;
		}

	}

	private int getCurVersionCode()
	{
		PackageInfo info;
		try
		{
			Context c = mContext.createPackageContext(ApkPluginProxyManager.PLUGINS_PACKAGE_NAME,
					Context.CONTEXT_INCLUDE_CODE
							| Context.CONTEXT_IGNORE_SECURITY);
			info = c.getPackageManager().getPackageInfo( c.getPackageName() , 0 );
			return Integer.parseInt(String.valueOf( info.versionCode ));
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	/*
	 * 
	 * @param url [String] (args.getString(0)) : 下载链接
	 * 
	 * @param package [String] (args.getString(1)) : 下载apk的包名
	 * 
	 * @param name [String] (args.getString(2)) : 下载apk名
	 */
	private void startDownApkService(Context context, final JSONArray args) {
		Intent intent = null;
		try {
			String url = args.getString(0);
			String packagename = args.getString(1);
			String apkname = args.getString(2);

			try {
				intent = new Intent();
				intent.putExtra("down_url", url);
				intent.putExtra("packageName", packagename);
				intent.putExtra("apkName", apkname);
				intent.setClassName(mContext.getPackageName(),
						"com.cooeelock.core.plugin.DownloadService");
				context.startService(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JSONException ex) {
			mCallbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.JSON_EXCEPTION));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isPluginAPKInstalled(Context context,
			String packageName) {
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(packageName, 0);
			boolean appStatus = appInfo.enabled;
			return appStatus;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean isPluginApkNeedUpdate(int version) {
		if (version > getCurVersionCode()) {
			return true;
		}
		return false;
	}

	private void handleExecute(final String action, final JSONArray args) {
		ApkPluginProxyManager.getInstance().setLockAuthority(mContext.getPackageName());
		mHandleRst = ApkPluginProxyManager.getInstance().execute(action, args);

		switch (mHandleRst) {
		case EXECUTE_RESULT_NOT_HANDLE:
			mCallbackContext.error(EXECUTE_RESULT_NOT_HANDLE);
			break;

		case EXECUTE_RESULT_HANDLE_FAIL:
			mCallbackContext.error(EXECUTE_RESULT_HANDLE_FAIL);
			break;

		case EXECUTE_RESULT_HANDLE_SUCCESS:
			mCallbackContext.success(EXECUTE_RESULT_HANDLE_SUCCESS);
			break;
		}
	}

	private void sendJS(final String js) {
		if (cordova.getActivity() != null) {
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl(js);
				}
			});
		} else if (cordova.getCordovaWrap() != null) {
			cordova.getCordovaWrap().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl(js);
				}
			});
		}
	}

	/**
	 * Called when the system is about to start resuming a previous activity.
	 * 
	 * @param multitasking
	 *            Flag indicating if multitasking is turned on for app
	 */
	public void onPause(boolean multitasking) {
		ApkPluginProxyManager.getInstance().onPause(multitasking);
	}

	/**
	 * Called when the activity will start interacting with the user.
	 * 
	 * @param multitasking
	 *            Flag indicating if multitasking is turned on for app
	 */
	public void onResume(boolean multitasking) {
		ApkPluginProxyManager.getInstance().onResume(multitasking);
	}

	/**
	 * Called when the activity is becoming visible to the user.
	 */
	public void onStart() {
		ApkPluginProxyManager.getInstance().onStart();
	}

	/**
	 * Called when the activity is no longer visible to the user.
	 */
	public void onStop() {
		ApkPluginProxyManager.getInstance().onStop();
	}

	/**
	 * Called when the activity receives a new intent.
	 */
	public void onNewIntent(Intent intent) {
		ApkPluginProxyManager.getInstance().onNewIntent(intent);
	}

	/**
	 * The final call you receive before your activity is destroyed.
	 */
	public void onDestroy() {
		ApkPluginProxyManager.getInstance().onDestroy();

		if (mJsdataReceiver != null) {
			mContext.unregisterReceiver(mJsdataReceiver);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		ApkPluginProxyManager.getInstance().onActivityResult(requestCode,
				resultCode, intent);
	}

	public Boolean shouldAllowRequest(String url) {
		return ApkPluginProxyManager.getInstance().shouldAllowRequest(url);
	}

	/**
	 * Hook for blocking navigation by the Cordova WebView. This applies both to
	 * top-level and iframe navigations.
	 * 
	 * This will be called when the WebView's needs to know whether to navigate
	 * to a new page. Return false to block the navigation: if any plugin
	 * returns false, Cordova will block the navigation. If all plugins return
	 * null, the default policy will be enforced. It at least one plugin returns
	 * true, and no plugins return false, then the navigation will proceed.
	 */
	public Boolean shouldAllowNavigation(String url) {
		return ApkPluginProxyManager.getInstance().shouldAllowNavigation(url);
	}

	/**
	 * Hook for blocking the launching of Intents by the Cordova application.
	 * 
	 * This will be called when the WebView will not navigate to a page, but
	 * could launch an intent to handle the URL. Return false to block this: if
	 * any plugin returns false, Cordova will block the navigation. If all
	 * plugins return null, the default policy will be enforced. If at least one
	 * plugin returns true, and no plugins return false, then the URL will be
	 * opened.
	 */
	public Boolean shouldOpenExternalUrl(String url) {
		return ApkPluginProxyManager.getInstance().shouldOpenExternalUrl(url);
	}

	/**
	 * Allows plugins to handle a link being clicked. Return true here to cancel
	 * the navigation.
	 * 
	 * @param url
	 *            The URL that is trying to be loaded in the Cordova webview.
	 * @return Return true to prevent the URL from loading. Default is false.
	 */
	public boolean onOverrideUrlLoading(String url) {
		return ApkPluginProxyManager.getInstance().onOverrideUrlLoading(url);
	}

	/**
	 * Hook for redirecting requests. Applies to WebView requests as well as
	 * requests made by plugins. To handle the request directly, return a URI in
	 * the form:
	 * 
	 * cdvplugin://pluginId/...
	 * 
	 * And implement handleOpenForRead(). To make this easier, use the
	 * toPluginUri() and fromPluginUri() helpers:
	 * 
	 * public Uri remapUri(Uri uri) { return toPluginUri(uri); }
	 * 
	 * public CordovaResourceApi.OpenForReadResult handleOpenForRead(Uri uri)
	 * throws IOException { Uri origUri = fromPluginUri(uri); ... }
	 */
	public Uri remapUri(Uri uri) {
		return ApkPluginProxyManager.getInstance().remapUri(uri);
	}

	/**
	 * Called by the system when the device configuration changes while your
	 * activity is running.
	 * 
	 * @param newConfig
	 *            The new device configuration
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		ApkPluginProxyManager.getInstance().onConfigurationChanged(newConfig);
	}
}
