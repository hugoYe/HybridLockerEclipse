package com.cooee.cordova.plugins.mobiledata;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Hugo.ye on 2015/10/29.
 */
public class MobileDataWizard extends CordovaPlugin {

	private static final String TAG = "MobileDataWizard";

	private static final String ACTION_IS_MOBILE_DATA_ENABLED = "isMobileDataEnabled";
	private static final String ACTION_click_MOBILE_DATA = "clickMobileData";

	private ConnectivityManager mConnectivityManager;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		if (cordova.getActivity() != null) {
			this.mConnectivityManager = (ConnectivityManager) cordova
					.getActivity().getSystemService(
							Context.CONNECTIVITY_SERVICE);
		} else if (cordova.getContext() != null) {
			this.mConnectivityManager = (ConnectivityManager) cordova
					.getContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
	}

	@Override
	public boolean execute(String action, JSONArray data,
			CallbackContext callbackContext) throws JSONException {

		if (action.equals(ACTION_IS_MOBILE_DATA_ENABLED)) {
			return this.isMobileDataEnabled(callbackContext);
		} else if (action.equals(ACTION_click_MOBILE_DATA)) {
			this.clickMobileData();
			return true;
		}

		return false;
	}

	public boolean isMobileDataEnabled(CallbackContext callbackContext) {
		boolean isEnabled = false;
		try {
			// 对于没有SIM卡的手机做相应的处理
			TelephonyManager tm = null;
			if (cordova.getActivity() != null) {
				tm = (TelephonyManager) cordova.getActivity().getSystemService(
						Context.TELEPHONY_SERVICE);
			} else if (cordova.getContext() != null) {
				tm = (TelephonyManager) cordova.getContext().getSystemService(
						Context.TELEPHONY_SERVICE);
			}
			if (tm == null
					|| TelephonyManager.SIM_STATE_UNKNOWN == tm.getSimState()
					|| tm.getNetworkOperatorName().equals("")
					|| tm.getNetworkType() == 0) {
				callbackContext.success(isEnabled ? "1" : "0");
				return isEnabled;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			Method getMethod = mConnectivityManager.getClass().getMethod(
					"getMobileDataEnabled");
			getMethod.setAccessible(true);
			isEnabled = (Boolean) getMethod.invoke(mConnectivityManager);
			callbackContext.success(isEnabled ? "1" : "0");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isEnabled;
	}

	public void clickMobileData() {
		Log.e(TAG, "clickMobileData");
		if (mConnectivityManager == null) {
			return;
		}
		TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(cordova
				.getActivity());
		boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
		boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
		if (!(isSIM1Ready || isSIM2Ready)) {
			return;
		}
		// 对于5.0以上的点击mobile 直接进入移动数据统计界面
		if (Build.VERSION.SDK_INT > 20) {
			try {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setComponent(new ComponentName("com.android.settings",
						"com.android.settings.Settings$DataUsageSummaryActivity"));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				if (cordova.getActivity() != null) {
					cordova.getActivity().startActivity(intent);
				} else if (cordova.getContext() != null) {
					cordova.getContext().startActivity(intent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		// 对于5.0以下的点击mobile 直接设置移动数据
		try {
			Method getMethod = mConnectivityManager.getClass().getMethod(
					"getMobileDataEnabled");
			getMethod.setAccessible(true);
			boolean isEnabled = (Boolean) getMethod
					.invoke(mConnectivityManager);
			setMobileDataEnabled(!isEnabled);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setMobileDataEnabled(boolean state) {
		try {
			final Class conmanClass = Class.forName(mConnectivityManager
					.getClass().getName());
			final Field iConnectivityManagerField = conmanClass
					.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField
					.get(mConnectivityManager);
			final Class iConnectivityManagerClass = Class
					.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass
					.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);
			setMobileDataEnabledMethod.invoke(iConnectivityManager, state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 长按进入移动流量设置界面
	 */
	public void longClickMobileData() {
		// 对于5.0以上的点击mobile 直接进入移动数据设置界面
		if (Build.VERSION.SDK_INT >= 21) {
			if (cordova.getActivity() != null) {
				cordova.getActivity().startActivity(
						new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
			} else if (cordova.getContext() != null) {
				cordova.getContext().startActivity(
						new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
			}
			return;
		}
		// 对于5.0以下的点击mobile 直接进入移动数据统计界面
		try {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setComponent(new ComponentName("com.android.settings",
					"com.android.settings.Settings$DataUsageSummaryActivity"));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (cordova.getActivity() != null) {
				cordova.getActivity().startActivity(intent);
			} else if (cordova.getContext() != null) {
				cordova.getContext().startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
