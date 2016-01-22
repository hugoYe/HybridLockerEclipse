package com.cooeelock.core.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.coco.lock.favorites.AppInfo;
import com.cooee.control.center.module.R.string;
import com.cooee.control.center.module.base.FileUtils;
import com.cooee.control.center.module.base.StaticClass;
import com.cooee.control.center.module.base.ThreadUtil;
import com.cooee.control.center.module.base.Tools;
import com.cooee.cordova.plugins.UnlockListener;

public class JarPlugin extends CordovaPlugin {

	private final String TAG = "JarPlugin";

	private final String ACTION_SAVE_JSON_DATA = "com.cooeelock.save.jarplugin.jsondata";
	private final String ACTION_SAVE_RES_DATA = "com.cooeelock.save.jarplugin.resdata";
	private final String ACTION_GET_JSON_DATA = "com.cooeelock.get.jarplugin.jsondata";
	private final String ACTION_GET_RES_DATA = "com.cooeelock.get.jarplugin.resdata";

	private Context mContext;

	private final String ACTION_UNLOCK = "unlock";
	private static UnlockListener sUnlockListener;

	private BroadcastReceiver mJsdataReceiver;
	public final static String ACTION_COPY_JAR_SDCARD_TO_DATA = "com.cooee.copy.jar.sdcard.to.data";
	public final static String ACTION_COPY_JAR_ASSETS_TO_DATA = "com.cooee.copy.jar.assets.to.data";
	public final static String ACTION_COPY_JAR_TO_DATA_SUCCESS = "com.cooee.copy.jar.to.data.success";
	public final static String ACTION_LOAD_WEBVIEW = "com.cooee.load.webview";

	class JsdataBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_SAVE_JSON_DATA)) {
				saveJsonData();
			} else if (action.equals(ACTION_GET_JSON_DATA)) {
				String jsData = intent.getStringExtra("js_data");
				String jsDir = intent.getStringExtra("js_dir");
				String spDir = intent.getStringExtra("sp_dir");
				if (cordova.getRemoteContext() != null) {
					SharedPreferences sp = cordova.getRemoteContext()
							.getSharedPreferences(mContext.getPackageName(),
									Context.MODE_PRIVATE);
					String jsonDir = sp.getString(spDir, jsDir);
					sendJS("javascript:" + jsData + "('" + jsonDir + "');");
					Log.e(TAG, "########  jsonDir = " + jsonDir);
				} else {
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(mContext);
					String jsonDir = sp.getString(spDir, jsDir);
					sendJS("javascript:" + jsData + "('" + jsonDir + "');");
					Log.e(TAG, "########  jsonDir = " + jsonDir);
				}
			} else if (action.equals(StaticClass.ACTION_JAR_COPY_SUCCESS)) {
				sendJS("javascript:copyJarSuccess();");
			} else if (action.equals(ACTION_SAVE_RES_DATA)) {
				saveResData(intent.getStringExtra("down_success"),
						intent.getStringExtra("copy_path"));
			} else if (action.equals(ACTION_GET_RES_DATA)) {
				String jsData = intent.getStringExtra("res_data");
				String jsDir = intent.getStringExtra("res_dir");
				String spDir = intent.getStringExtra("res_sp");
				int num;
				if (cordova.getRemoteContext() != null) {
					SharedPreferences sp = cordova.getRemoteContext()
							.getSharedPreferences(mContext.getPackageName(),
									Context.MODE_PRIVATE);
					String jsonDir = sp.getString(spDir, jsDir);
					File file = new File(cordova.getRemoteContext()
							.getFilesDir()
							+ "/"
							+ mContext.getPackageName()
							+ "/"
							+ jsonDir);
					if (file.exists()) {
						num = file.list().length;
					} else {
						num = 0;
					}
					if (num > 0) {
						sendJS("javascript:" + jsData + "('" + num + "','"
								+ jsonDir + "');");
					}
					Log.e(TAG, "########  jsonDir = " + "javascript:" + jsData
							+ "('" + num + "','" + jsonDir + "');");
				} else {
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(mContext);
					String jsonDir = sp.getString(spDir, jsDir);
					File file = new File(mContext.getFilesDir() + "/" + jsonDir);
					if (file.exists()) {
						num = file.list().length;
					} else {
						num = 0;
					}
					if (num > 0) {
						sendJS("javascript:" + jsData + "('" + num + "','"
								+ jsonDir + "');");
					}
					Log.e(TAG, "########  jsonDir = " + "javascript:" + jsData
							+ "('" + num + "','" + jsonDir + "');");
				}
			}
		}
	}

	public static void setOnUnlockListener(UnlockListener unlockListener) {
		sUnlockListener = unlockListener;
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		if (cordova.getActivity() != null) {
			mContext = cordova.getActivity();
		} else if (cordova.getContext() != null) {
			mContext = cordova.getContext();
		}

		saveJsonData();

		mJsdataReceiver = new JsdataBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SAVE_JSON_DATA);
		filter.addAction(ACTION_GET_JSON_DATA);
		filter.addAction(ACTION_SAVE_RES_DATA);
		filter.addAction(ACTION_GET_RES_DATA);
		filter.addAction(StaticClass.ACTION_JAR_COPY_SUCCESS);
		mContext.registerReceiver(mJsdataReceiver, filter);
	}

	private void saveResData(String successFile, final String savePath) {
		final String sdFilesDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/h5lock/" + mContext.getPackageName();
		final File sdFile = new File(sdFilesDir + "/" + successFile);
		if (sdFile.exists()) {
			ThreadUtil.execute(new Runnable() {

				@Override
				public void run() {
					SharedPreferences sp;
					String resFilesDir;
					if (cordova.getRemoteContext() != null) {
						sp = cordova.getRemoteContext()
								.getSharedPreferences(
										mContext.getPackageName(),
										Context.MODE_PRIVATE);
					} else {
						sp = PreferenceManager
								.getDefaultSharedPreferences(mContext);
					}
					String filesDir = sp.getString(savePath + "_dir", savePath);
					if (filesDir.equals(savePath)) {
						resFilesDir = savePath + "1";
					} else {
						resFilesDir = savePath;
					}

					if (cordova.getRemoteContext() != null) {
						Log.v("update_complete", "true");
						File dir = new File(cordova.getRemoteContext()
								.getFilesDir()
								+ "/"
								+ mContext.getPackageName());
						// 先删除原有目录下所有文件
						FileUtils.deleteFile(new File(dir + "/" + filesDir));
						dir.mkdirs();
						String destDir = dir.getAbsolutePath() + "/"
								+ resFilesDir;
						String path = sdFilesDir + "/" + savePath;

						FileUtils.copySDDirToFiles(path, destDir);
					} else {
						File dir = mContext.getFilesDir();
						// 先删除原有目录下所有文件
						FileUtils.deleteFile(new File(dir + "/" + filesDir));
						String path = sdFilesDir + "/" + savePath;
						String destDir = dir.getAbsolutePath() + "/"
								+ resFilesDir;
						FileUtils.copySDDirToFiles(path, destDir);
					}
					sp.edit().putString(savePath + "_dir", resFilesDir)
							.commit();
					sdFile.delete();
					File sdDownFile = new File(sdFilesDir + "/" + savePath);
					if (sdDownFile.exists()) {
						FileUtils.deleteFile(sdDownFile);
					}
					sendJS("javascript:saveResDataSuccess();");
				}
			});
		}
	}

	private void saveJsonData() {
		ThreadUtil.execute(new Runnable() {

			@Override
			public void run() {
				String data = "";
				String name = "";
				String path = "";
				String filesDir = "";
				Cursor csr = null;
				try {
					ContentResolver resolver = mContext.getContentResolver();
					Uri contentUri = Uri.parse("content://"
							+ mContext.getPackageName() + ".plugin/jarplugin");
					Log.v("###********######", "getJsData contentUri"
							+ contentUri.toString());
					csr = resolver.query(contentUri, null, null, null, null);
					Log.v("###********######",
							"getJsData csr = " + csr.getCount());
					if (csr != null) {
						if (csr.getCount() > 0) {
							while (csr.moveToNext()) {
								data = csr.getString(csr
										.getColumnIndexOrThrow("data"));
								name = csr.getString(csr
										.getColumnIndexOrThrow("label"));
								path = csr.getString(csr
										.getColumnIndexOrThrow("path"));
								File file = null;
								SharedPreferences sp;
								if (cordova.getRemoteContext() != null) {
									sp = cordova.getRemoteContext()
											.getSharedPreferences(
													mContext.getPackageName(),
													Context.MODE_PRIVATE);
									String jsonDir = sp.getString(
											path + "_dir", path);
									if (jsonDir.equals(path)) {
										filesDir = path + "1";
									} else {
										filesDir = path;
									}
									String filePath = cordova
											.getRemoteContext().getFilesDir()
											+ "/"
											+ mContext.getPackageName()
											+ "/"
											+ sp.getString("html_dir", "www");
									FileUtils.deleteFile(new File(filePath
											+ "/" + jsonDir));
									file = new File(filePath + "/" + filesDir
											+ "/" + name + ".json");
								} else {
									sp = PreferenceManager
											.getDefaultSharedPreferences(mContext);
									String jsonDir = sp.getString(
											path + "_dir", path);
									if (jsonDir.equals(path)) {
										filesDir = path + "1";
									} else {
										filesDir = path;
									}
									String filePath = mContext.getFilesDir()
											+ "/"
											+ sp.getString("html_dir", "www");
									FileUtils.deleteFile(new File(filePath
											+ "/" + jsonDir));
									file = new File(filePath + "/" + filesDir
											+ "/" + name + ".json");
								}
								Log.v("###********######",
										"cordova.getRemoteContext() "
												+ cordova.getRemoteContext());
								Log.v("###********######",
										"file " + file.toString());
								if (!file.getParentFile().exists()) {// 判断父文件是否存在，如果不存在则创建
									file.getParentFile().mkdirs();
								}
								PrintStream out = null; // 打印流
								try {
									out = new PrintStream(new FileOutputStream(
											file)); // 实例化打印流对象
									out.print(data); // 输出数据
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} finally {
									if (out != null) { // 如果打印流不为空，则关闭打印流
										out.close();
									}
								}
								sp.edit().putString(path + "_dir", filesDir)
										.commit();
							}
							sendJS("javascript:saveJsonDataSuccess();");
							resolver.delete(contentUri, null, null);
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
			}
		});
	}

	@Override
	public boolean execute(final String action, final JSONArray args,
			final CallbackContext callbackContext) throws JSONException {

		Log.e(TAG, "########  JSONArray = " + args.toString());
		Log.e(TAG, "########  action = " + action);
		if (action.equals(ACTION_UNLOCK)) {
			if (sUnlockListener != null) {
				sUnlockListener.onUnlock();
			}
			return true;
		}

		Intent it = new Intent("com.cooee.start.service");
		it.setClassName(mContext.getPackageName(),
				"com.cooeelock.core.plugin.JarExecuteService");
		it.putExtra("jar_action", action);
		it.putExtra("jar_args", args.toString());
		mContext.startService(it);

		return true;
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
		Intent intent = new Intent("com.cooee.jar.onpause");
		intent.setClassName(mContext.getPackageName(),
				"com.cooeelock.core.plugin.JarExecuteService");
		intent.putExtra("key_value", multitasking);
		mContext.startService(intent);
		// JarPluginProxyManager.getInstance().onPause(multitasking);
	}

	/**
	 * Called when the activity will start interacting with the user.
	 * 
	 * @param multitasking
	 *            Flag indicating if multitasking is turned on for app
	 */
	public void onResume(boolean multitasking) {
		Intent intent = new Intent("com.cooee.jar.onresume");
		intent.setClassName(mContext.getPackageName(),
				"com.cooeelock.core.plugin.JarExecuteService");
		intent.putExtra("key_value", multitasking);
		mContext.startService(intent);
		// JarPluginProxyManager.getInstance().onResume(multitasking);
	}

	/**
	 * Called when the activity is becoming visible to the user.
	 */
	public void onStart() {
		Intent intent = new Intent("com.cooee.jar.onstart");
		intent.setClassName(mContext.getPackageName(),
				"com.cooeelock.core.plugin.JarExecuteService");
		mContext.startService(intent);
		// JarPluginProxyManager.getInstance().onStart();
	}

	/**
	 * Called when the activity is no longer visible to the user.
	 */
	public void onStop() {
		Intent intent = new Intent("com.cooee.jar.onstop");
		intent.setClassName(mContext.getPackageName(),
				"com.cooeelock.core.plugin.JarExecuteService");
		mContext.startService(intent);
		// JarPluginProxyManager.getInstance().onStop();
	}

	/**
	 * Called when the activity receives a new intent.
	 */
	public void onNewIntent(Intent intent) {
		Intent intent1 = new Intent("com.cooee.jar.onnewintent");
		intent1.setClassName(mContext.getPackageName(),
				"com.cooeelock.core.plugin.JarExecuteService");
		intent1.putExtra("key_value", intent);
		mContext.startService(intent1);
		// JarPluginProxyManager.getInstance().onNewIntent(intent);
	}

	/**
	 * The final call you receive before your activity is destroyed.
	 */
	public void onDestroy() {
		Intent intent = new Intent("com.cooee.jar.ondestroy");
		intent.setClassName(mContext.getPackageName(),
				"com.cooeelock.core.plugin.JarExecuteService");
		mContext.startService(intent);
		// JarPluginProxyManager.getInstance().onDestroy();

		if (mJsdataReceiver != null) {
			mContext.unregisterReceiver(mJsdataReceiver);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Intent intent1 = new Intent("com.cooee.jar.onactivityresult");
		intent1.setClassName(mContext.getPackageName(),
				"com.cooeelock.core.plugin.JarExecuteService");
		intent1.putExtra("key_value1", requestCode);
		intent1.putExtra("key_value2", resultCode);
		intent1.putExtra("key_value3", intent);
		mContext.startService(intent1);
		// JarPluginProxyManager.getInstance().onActivityResult(requestCode,
		// resultCode, intent);
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
