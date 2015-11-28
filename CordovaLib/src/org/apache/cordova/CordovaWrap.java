package org.apache.cordova;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

/**
 * Created by Hugo.ye on 2015/11/4.
 */
public class CordovaWrap {

	public static String TAG = "CordovaWrap";

	private Context mContext;

	private Context mRemoteContext;

	// The webview for our app
	protected CordovaWebView appView;

	private static int ACTIVITY_STARTING = 0;
	private static int ACTIVITY_RUNNING = 1;
	private static int ACTIVITY_EXITING = 2;

	// Keep app running when pause is received. (default = true)
	// If true, then the JavaScript and native code continue to run in the
	// background
	// when another application (activity) is started.
	protected boolean keepRunning = true;

	// Flag to keep immersive mode if set to fullscreen
	protected boolean immersiveMode;

	// Read from config.xml:
	protected CordovaPreferences preferences;
	public String launchUrl;
	protected ArrayList<PluginEntry> pluginEntries;
	protected CordovaInterfaceImpl cordovaInterface;

	final Handler mHandler = new Handler();

	public final void runOnUiThread(Runnable action) {
		LOG.e(TAG, "######## runOnUiThread, mHandler = " + mHandler
				+ ", loop = " + mHandler.getLooper());
		mHandler.post(action);
	}

	public CordovaWrap(Context context, Context remoteContext) {
		this.mContext = context;
		this.mRemoteContext = remoteContext;
	}

	public void onCreate(Bundle savedInstanceState) {
		LOG.i(TAG, "Apache Cordova native platform version "
				+ CordovaWebView.CORDOVA_VERSION + " is starting");
		LOG.d(TAG, "CordovaActivity.onCreate()");

		// need to activate preferences before super.onCreate to avoid
		// "requestFeature() must be called before adding content" exception
		loadConfig();
		// removed by Hugo.ye begin
		if (!preferences.getBoolean("ShowTitle", false)) {
			if (mContext instanceof Activity) {
				((Activity) mContext).getWindow().requestFeature(
						Window.FEATURE_NO_TITLE);
			}
		}

		if (preferences.getBoolean("SetFullscreen", false)) {
			Log.d(TAG,
					"The SetFullscreen configuration is deprecated in favor of Fullscreen, and will be removed in a future version.");
			preferences.set("Fullscreen", true);
		}
		if (preferences.getBoolean("Fullscreen", false)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				immersiveMode = true;
			} else {
				if (mContext instanceof Activity) {
					((Activity) mContext).getWindow().setFlags(
							WindowManager.LayoutParams.FLAG_FULLSCREEN,
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
			}
		} else {
			if (mContext instanceof Activity) {
				((Activity) mContext).getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			}
		}

		// super.onCreate(savedInstanceState);
		// removed by Hugo.ye end

		cordovaInterface = makeCordovaInterface();
		if (savedInstanceState != null) {
			cordovaInterface.restoreInstanceState(savedInstanceState);
		}
	}

	// removed by Hugo.ye begin
	// protected void init() {
	// appView = makeWebView();
	// createViews();
	// if (!appView.isInitialized()) {
	// appView.init(cordovaInterface, pluginEntries, preferences);
	// }
	// cordovaInterface.onCordovaInit(appView.getPluginManager());
	//
	// // Wire the hardware volume controls to control media if desired.
	// String volumePref = preferences.getString("DefaultVolumeStream", "");
	// if ("media".equals(volumePref.toLowerCase(Locale.ENGLISH))) {
	// setVolumeControlStream(AudioManager.STREAM_MUSIC);
	// }
	// }
	// removed by Hugo.ye end

	@SuppressWarnings("deprecation")
	protected void loadConfig() {
		ConfigXmlParser parser = new ConfigXmlParser();
		parser.parse(mContext);
		preferences = parser.getPreferences();
		// preferences.setPreferencesBundle(getIntent().getExtras()); // removed
		// by Hugo.ye
		launchUrl = parser.getLaunchUrl();
		pluginEntries = parser.getPluginEntries();
		Config.parser = parser;
	}

	// removed by Hugo.ye begin
	// Suppressing warnings in AndroidStudio
	// @SuppressWarnings({"deprecation", "ResourceType"})
	// protected void createViews() {
	// //Why are we setting a constant as the ID? This should be investigated
	// appView.getView().setId(100);
	// appView.getView().setLayoutParams(new FrameLayout.LayoutParams(
	// ViewGroup.LayoutParams.MATCH_PARENT,
	// ViewGroup.LayoutParams.MATCH_PARENT));
	//
	// setContentView(appView.getView());
	//
	// if (preferences.contains("BackgroundColor")) {
	// int backgroundColor = preferences.getInteger("BackgroundColor",
	// Color.BLACK);
	// // Background of activity:
	// appView.getView().setBackgroundColor(backgroundColor);
	// }
	//
	// appView.getView().requestFocusFromTouch();
	// }
	// removed by Hugo.ye end

	/**
	 * Construct the default web view object.
	 * <p/>
	 * Override this to customize the webview that is used.
	 */
	protected CordovaWebView makeWebView() {
		return new CordovaWebViewImpl(makeWebViewEngine());
	}

	protected CordovaWebViewEngine makeWebViewEngine() {
		return CordovaWebViewImpl.createEngineWrap(mContext, mRemoteContext,
				preferences);
	}

	protected CordovaInterfaceImpl makeCordovaInterface() {

		return new CordovaInterfaceImpl(mContext, mRemoteContext, this) {
			@Override
			public Object onMessage(String id, Object data) {
				// Plumb this to CordovaActivity.onMessage for backwards
				// compatibility
				return CordovaWrap.this.onMessage(id, data);
			}
		};
	}

	/**
	 * Load the url into the webview.
	 */
	// public void loadUrl(String url) {
	// if (appView == null) {
	// init();
	// }
	//
	// // If keepRunning
	// this.keepRunning = preferences.getBoolean("KeepRunning", true);
	//
	// appView.loadUrlIntoView(url, true);
	// }

	/**
	 * add by Hugo.ye begin
	 */
	public View loadWebViewUrl(String url) {
		if (appView == null) {
			appView = makeWebView();
			// Why are we setting a constant as the ID? This should be
			// investigated
			appView.getView().setId(100);
			appView.getView().setLayoutParams(
					new FrameLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.MATCH_PARENT));

			if (preferences.contains("BackgroundColor")) {
				int backgroundColor = preferences.getInteger("BackgroundColor",
						Color.BLACK);
				// Background of activity:
				appView.getView().setBackgroundColor(backgroundColor);
			}

			appView.getView().requestFocusFromTouch();

			if (!appView.isInitialized()) {
				appView.init(cordovaInterface, pluginEntries, preferences);
			}
			cordovaInterface.onCordovaInit(appView.getPluginManager());

			// Wire the hardware volume controls to control media if desired.
			String volumePref = preferences
					.getString("DefaultVolumeStream", "");
			if ("media".equals(volumePref.toLowerCase(Locale.ENGLISH))) {
				// setVolumeControlStream(AudioManager.STREAM_MUSIC);
			}
		}

		// If keepRunning
		this.keepRunning = preferences.getBoolean("KeepRunning", true);

		appView.loadUrlIntoView(url, true);

		return appView.getView();
	}

	/**
	 * add by Hugo.ye end
	 * */

	/**
	 * Called when the system is about to start resuming a previous activity.
	 */
	public void onPause() {
		LOG.d(TAG, "Paused the activity.");

		if (this.appView != null) {
			// CB-9382 If there is an activity that started for result and main
			// activity is waiting for callback
			// result, we shoudn't stop WebView Javascript timers, as activity
			// for result might be using them
			boolean keepRunning = this.keepRunning
					|| this.cordovaInterface.activityResultCallback != null;
			this.appView.handlePause(keepRunning);
		}
	}

	/**
	 * Called when the activity receives a new intent
	 **/
	public void onNewIntent(Intent intent) {
		// Forward to plugins
		if (this.appView != null) {
			this.appView.onNewIntent(intent);
		}
	}

	/**
	 * Called when the activity will start interacting with the user.
	 */
	public void onResume() {
		LOG.d(TAG, "Resumed the activity.");

		if (this.appView == null) {
			return;
		}
		// Force window to have focus, so application always
		// receive user input. Workaround for some devices (Samsung Galaxy Note
		// 3 at least)
		// this.getWindow().getDecorView().requestFocus();

		this.appView.handleResume(this.keepRunning);
	}

	/**
	 * Called when the activity is no longer visible to the user.
	 */
	public void onStop() {
		LOG.d(TAG, "Stopped the activity.");

		if (this.appView == null) {
			return;
		}
		this.appView.handleStop();
	}

	/**
	 * Called when the activity is becoming visible to the user.
	 */
	public void onStart() {
		LOG.d(TAG, "Started the activity.");

		if (this.appView == null) {
			return;
		}
		this.appView.handleStart();
	}

	/**
	 * The final call you receive before your activity is destroyed.
	 */
	public void onDestroy() {
		LOG.d(TAG, "CordovaActivity.onDestroy()");

		if (this.appView != null) {
			appView.handleDestroy();
		}
	}

	public void startActivityForResult(Intent intent, int requestCode,
			Bundle options) {
		// Capture requestCode here so that it is captured in the
		// setActivityResultCallback() case.
		cordovaInterface.setActivityResultRequestCode(requestCode);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		LOG.d(TAG, "Incoming Result. Request code = " + requestCode);
		cordovaInterface.onActivityResult(requestCode, resultCode, intent);
	}

	/**
	 * Report an error to the host application. These errors are unrecoverable
	 * (i.e. the main resource is unavailable). The errorCode parameter
	 * corresponds to one of the ERROR_* constants.
	 * 
	 * @param errorCode
	 *            The error code corresponding to an ERROR_* value.
	 * @param description
	 *            A String describing the error.
	 * @param failingUrl
	 *            The url that failed to load.
	 */
	public void onReceivedError(final int errorCode, final String description,
			final String failingUrl) {
		final CordovaWrap me = this;

		// If errorUrl specified, then load it
		final String errorUrl = preferences.getString("errorUrl", null);
		if ((errorUrl != null) && (!failingUrl.equals(errorUrl))
				&& (appView != null)) {
			// Load URL on UI thread
			me.runOnUiThread(new Runnable() {
				public void run() {
					me.appView.showWebPage(errorUrl, false, true, null);
				}
			});
		}
		// If not, then display error dialog
		else {
			final boolean exit = !(errorCode == WebViewClient.ERROR_HOST_LOOKUP);
			me.runOnUiThread(new Runnable() {
				public void run() {
					if (exit) {
						me.appView.getView().setVisibility(View.GONE);
						me.displayError("Application Error", description + " ("
								+ failingUrl + ")", "OK", exit);
					}
				}
			});
		}
	}

	/**
	 * Display an error dialog and optionally exit application.
	 */
	public void displayError(final String title, final String message,
			final String button, final boolean exit) {
		final CordovaWrap me = this;
		me.runOnUiThread(new Runnable() {
			public void run() {
				try {
					AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
					dlg.setMessage(message);
					dlg.setTitle(title);
					dlg.setCancelable(false);
					dlg.setPositiveButton(button,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									if (exit) {
										// finish();
									}
								}
							});
					dlg.create();
					dlg.show();
				} catch (Exception e) {
					// finish();
				}
			}
		});
	}

	// /*
	// * Hook in Cordova for menu plugins
	// */
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// if (appView != null) {
	// appView.getPluginManager().postMessage("onCreateOptionsMenu", menu);
	// }
	// return super.onCreateOptionsMenu(menu);
	// }
	//
	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// if (appView != null) {
	// appView.getPluginManager().postMessage("onPrepareOptionsMenu", menu);
	// }
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// if (appView != null) {
	// appView.getPluginManager().postMessage("onOptionsItemSelected", item);
	// }
	// return true;
	// }

	/**
	 * Called when a message is sent to plugin.
	 * 
	 * @param id
	 *            The message id
	 * @param data
	 *            The message data
	 * @return Object or null
	 */
	public Object onMessage(String id, Object data) {
		if ("onReceivedError".equals(id)) {
			JSONObject d = (JSONObject) data;
			try {
				this.onReceivedError(d.getInt("errorCode"),
						d.getString("description"), d.getString("url"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if ("exit".equals(id)) {
			// finish();
		}
		return null;
	}

	protected void onSaveInstanceState(Bundle outState) {
		cordovaInterface.onSaveInstanceState(outState);
	}

	/**
	 * Called by the system when the device configuration changes while your
	 * activity is running.
	 * 
	 * @param newConfig
	 *            The new device configuration
	 */
	// @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// super.onConfigurationChanged(newConfig);
		if (this.appView == null) {
			return;
		}
		PluginManager pm = this.appView.getPluginManager();
		if (pm != null) {
			pm.onConfigurationChanged(newConfig);
		}
	}
}
