package com.cooeelock.core.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;

import com.cooee.cordova.plugins.UnlockListener;

public class JarPlugin extends CordovaPlugin {

	private final String TAG = "JarPlugin";

	private final int EXECUTE_RESULT_NOT_HANDLE = -1;
	private final int EXECUTE_RESULT_HANDLE_FAIL = 0;
	private final int EXECUTE_RESULT_HANDLE_SUCCESS = 1;

	private final String ACTION_JS_DATA = "com.cooeelock.core.jarplugin.jsdata";

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
	}

	@Override
	public boolean execute(final String action, final JSONArray args,
			final CallbackContext callbackContext) throws JSONException {

		mCallbackContext = callbackContext;

		Log.e(TAG, "########  JSONArray = " + args.toString());

		int version = args.getInt(0);
		if (isJarPluginNeedUpdate(version)) {
			// 更新apk
			Log.i(TAG, "######## 更新Jar !!!!!!");
			return false;
		}

		Log.e(TAG, "######## startService");

		Intent it = new Intent();
		it.setClassName(mContext.getPackageName(),
				"com.cooeelock.core.plugin.JarExecuteService");
		it.putExtra("key_action", action);
		it.putExtra("key_jsonarray", args.toString());
		mContext.startService(it);

		if (sUnlockListener != null) {
			sUnlockListener.onUnlock();
		}

		return true;
	}

	private boolean isJarPluginNeedUpdate(int version) {
		// if (version > 0) {
		// return true;
		// }

		return false;
	}

	private void handleExecute(final String action, final JSONArray args) {
		mHandleRst = JarPluginProxyManager.getInstance().execute(action, args);

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
		JarPluginProxyManager.getInstance().onPause(multitasking);
	}

	/**
	 * Called when the activity will start interacting with the user.
	 * 
	 * @param multitasking
	 *            Flag indicating if multitasking is turned on for app
	 */
	public void onResume(boolean multitasking) {
		JarPluginProxyManager.getInstance().onResume(multitasking);
	}

	/**
	 * Called when the activity is becoming visible to the user.
	 */
	public void onStart() {
		JarPluginProxyManager.getInstance().onStart();
	}

	/**
	 * Called when the activity is no longer visible to the user.
	 */
	public void onStop() {
		JarPluginProxyManager.getInstance().onStop();
	}

	/**
	 * Called when the activity receives a new intent.
	 */
	public void onNewIntent(Intent intent) {
		JarPluginProxyManager.getInstance().onNewIntent(intent);
	}

	/**
	 * The final call you receive before your activity is destroyed.
	 */
	public void onDestroy() {
		JarPluginProxyManager.getInstance().onDestroy();

		if (mJsdataReceiver != null) {
			mContext.unregisterReceiver(mJsdataReceiver);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		JarPluginProxyManager.getInstance().onActivityResult(requestCode,
				resultCode, intent);
	}

	public Boolean shouldAllowRequest(String url) {
		return JarPluginProxyManager.getInstance().shouldAllowRequest(url);
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
		return JarPluginProxyManager.getInstance().shouldAllowNavigation(url);
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
		return JarPluginProxyManager.getInstance().shouldOpenExternalUrl(url);
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
		return JarPluginProxyManager.getInstance().onOverrideUrlLoading(url);
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
		return JarPluginProxyManager.getInstance().remapUri(uri);
	}

	/**
	 * Called by the system when the device configuration changes while your
	 * activity is running.
	 * 
	 * @param newConfig
	 *            The new device configuration
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		JarPluginProxyManager.getInstance().onConfigurationChanged(newConfig);
	}
}
