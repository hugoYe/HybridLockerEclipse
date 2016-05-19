package com.cooeeui.cordova.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Hugo.ye on 2015/10/31.
 */
public class TouchEventPrevent extends CordovaPlugin {

	private static final String TAG = "TouchEventPreventPlugin";

	private static final String ACTION_PREVENT_TOUCH_SELF = "preventTouchSelf";

	public static boolean preventWebTouchEvent;

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {

		if (action.equals(ACTION_PREVENT_TOUCH_SELF)) {
			preventEventSelf();
			return true;
		}

		return false;
	}

	private void preventEventSelf() {
		this.preventWebTouchEvent = true;
	}

}
