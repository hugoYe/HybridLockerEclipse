package com.cooee.cordova.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class EventStatistics extends CordovaPlugin {

	private static final String TAG = "EventStatisticsPlugin";

	public final String ACTION_STATISTICS_EVENT = "onEvent";
	private CallbackContext mCallbackContext;

	@Override
	public boolean execute(String action, final JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		mCallbackContext = callbackContext;
		Log.e(TAG, "EventStatistics onEvent-java222");
		if (action.equals(ACTION_STATISTICS_EVENT)) {
			onEvent(args);
			return true;
		}
		return false;
	}

	/**
	 * js中用统计操作事件类型的接口
	 * 
	 * @param eventType
	 *            [String] (args.getString(0)) : 统计的事件类型 ---> example: "0036"
	 * 
	 */
	private void onEvent(final JSONArray args) {
		Log.e(TAG, "onEvent-java222");
		cordova.getThreadPool().execute(new Runnable() {
			
			@Override
			public void run() {
				if (cordova.getActivity() != null) {
					onEventImpl(args, cordova.getActivity());
				} else if (cordova.getContext() != null) {
					onEventImpl(args, cordova.getContext());
				}
			}
		});
	}

	private void onEventImpl(final JSONArray args, Context context) {
		Intent intent = null;
		try {
			String eventType = args.getString(0);

			try {
				intent = new Intent();
				intent.putExtra("EventType", eventType);
				intent.setClassName(context.getPackageName(),
						"com.cooee.lock.statistics.StaticClass");
			} catch (Exception e) {
				e.printStackTrace();
			}
			context.startService(intent);
		} catch (JSONException ex) {
			mCallbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.JSON_EXCEPTION));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
