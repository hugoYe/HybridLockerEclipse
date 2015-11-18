package com.cooee.cordova.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

public class BluetoothStatus extends CordovaPlugin {
	private static CordovaWebView mwebView;
	private static CordovaInterface mcordova;

	private static final String LOG_TAG = "BluetoothStatusPlugin";
	private BluetoothAdapter bluetoothAdapter;
	private CallbackContext callbackContext;
	private Context mContext;

	private static UnlockListener sUnlockListener;

	public static void setOnUnlockListener(UnlockListener unlockListener) {
		sUnlockListener = unlockListener;
	}

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {

		this.callbackContext = callbackContext;
		if (action.equals("enableBT")) {
			enableBT();
			return true;
		} else if (action.equals("disableBT")) {
			disableBT();
			return true;
		} else if (action.equals("promptForBT")) {
			promptForBT();
			return true;
		} else if (action.equals("initPlugin")) {
			initPlugin();
			return true;
		} else if (action.equals("isBlueEnabled")) {
			isBlueEnabled(callbackContext);
			return true;
		} else if (action.equals("entryBluetoothSettings")) {
			entryBluetoothSettings();
			return true;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mContext.unregisterReceiver(mReceiver);

	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		mwebView = super.webView;
		mcordova = cordova;

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// Register for broadcasts on BluetoothAdapter state change
		IntentFilter filter = new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED);
		if (mcordova.getActivity() != null) {
			mContext = mcordova.getActivity();
		} else if (mcordova.getContext() != null) {
			mContext = mcordova.getContext();
		}
		mContext.registerReceiver(mReceiver, filter);
	}

	private void enableBT() {
		// enable bluetooth without prompting
		if (bluetoothAdapter == null) {
			Log.e(LOG_TAG, "Bluetooth is not supported");
		} else if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}
	}

	private void disableBT() {
		// disable bluetooth without prompting
		if (bluetoothAdapter == null) {
			Log.e(LOG_TAG, "Bluetooth is not supported");
		} else if (bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.disable();
		}
	}

	private boolean isBlueEnabled(CallbackContext callbackContext) {
		boolean isEnabled = bluetoothAdapter.isEnabled();
		callbackContext.success(isEnabled ? "1" : "0");
		return isEnabled;
	}

	public void entryBluetoothSettings() {
		try {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setComponent(new ComponentName("com.android.settings",
					"com.android.settings.Settings$BluetoothSettingsActivity"));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (mContext != null) {
				mContext.startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (mContext != null) {
				mContext.startActivity(new Intent(
						Settings.ACTION_BLUETOOTH_SETTINGS));
			}
		}

		if (sUnlockListener != null) {
			sUnlockListener.onUnlock();
		}

	}

	private void promptForBT() {
		// prompt user for enabling bluetooth
		Intent enableBTIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
		if (mContext != null) {
			mContext.startActivity(enableBTIntent);
		}
	}

	private void initPlugin() {
		// test if B supported
		if (bluetoothAdapter == null) {
			Log.e(LOG_TAG, "Bluetooth is not supported");
		} else {
			Log.e(LOG_TAG, "Bluetooth is supported");

			sendJS("javascript:cordova.plugins.BluetoothStatus.hasBT = true;");

			PackageManager manager = null;
			if (mcordova.getActivity() != null) {
				manager = mcordova.getActivity().getPackageManager();
			} else if (mcordova.getContext() != null) {
				manager = mcordova.getContext().getPackageManager();
			}
			// test if BLE supported
			if (!manager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
				Log.e(LOG_TAG, "BluetoothLE is not supported");
			} else {
				Log.e(LOG_TAG, "BluetoothLE is supported");
				sendJS("javascript:cordova.plugins.BluetoothStatus.hasBTLE = true;");
			}

			// test if BT enabled
			if (bluetoothAdapter.isEnabled()) {
				Log.e(LOG_TAG, "Bluetooth is enabled");

				sendJS("javascript:cordova.plugins.BluetoothStatus.BTenabled = true;");
				sendJS("javascript:cordova.fireWindowEvent('BluetoothStatus.enabled');");
			} else {
				Log.e(LOG_TAG, "Bluetooth is not enabled");
			}
		}
	}

	private void sendJS(final String js) {
		if (mcordova.getActivity() != null) {
			mcordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mwebView.loadUrl(js);
				}
			});
		} else if (mcordova.getCordovaWrap() != null) {
			mcordova.getCordovaWrap().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mwebView.loadUrl(js);
				}
			});
		}
	}

	// broadcast receiver for BT intent changes
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				switch (state) {
				case BluetoothAdapter.STATE_OFF:
					Log.e(LOG_TAG, "Bluetooth was disabled");

					sendJS("javascript:onBluetoothChanged(false);");
					sendJS("javascript:cordova.plugins.BluetoothStatus.BTenabled = false;");
					sendJS("javascript:cordova.fireWindowEvent('BluetoothStatus.disabled');");

					break;
				case BluetoothAdapter.STATE_ON:
					Log.e(LOG_TAG, "Bluetooth was enabled");

					sendJS("javascript:onBluetoothChanged(true);");
					sendJS("javascript:cordova.plugins.BluetoothStatus.BTenabled = true;");
					sendJS("javascript:cordova.fireWindowEvent('BluetoothStatus.enabled');");

					break;
				}
			}
		}
	};
}