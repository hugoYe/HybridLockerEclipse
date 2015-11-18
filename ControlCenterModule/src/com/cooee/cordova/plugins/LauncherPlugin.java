package com.cooee.cordova.plugins;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;
import cool.sdk.common.CoolHttpClient;
import cool.sdk.common.CoolHttpClient.ResultEntity;
import cool.sdk.common.JsonUtil;
import dalvik.system.DexClassLoader;

public class LauncherPlugin extends CordovaPlugin {
	public static final String PREFS_KEY = "h5_plugin";
	public final String ACTION_START_ACTIVITY = "startActivity";
	public final String ACTION_SEARCH = "search";
	public final String ACTION_START_URL = "startUrl";
	public final String ACTION_DOWNLOAD_PLUGIN = "downloadServerPlugin";
	public final String ACTION_CHECK_VERSION = "checkServerVersion";
	public final String ACTION_GET_CURRENT_LANGUAGE = "getCurrentLanguage";
	public final String ACTION_GET_TOUTIAO_ACCESS_TOKEN_PARAMS = "getTouTiaoAccessTokenParams";
	public final String ACTION_GET_PREFS = "getPrefs";
	public final String ACTION_PUT_PREFS = "putPrefs";
	public final String ACTION_TOAST = "toast";

	@Override
	public boolean execute(String action, final JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		final String params = args.getString(0);
		int handle = -1;// PluginProxyManager.getInstance().execute(action,
						// params);
		if (handle == -1) {
			if (action.equals(ACTION_START_ACTIVITY)) {
				handleStartActivity(params);
				return true;
			}
			if (action.equals(ACTION_SEARCH)) {
				handleSearch(params);
				return true;
			}
			if (action.equals(ACTION_START_URL)) {
				handleStartUrl(params);
				return true;
			}
			if (action.equals(ACTION_DOWNLOAD_PLUGIN)) {
				handleDownloadPlugin(params);
				return true;
			}
			if (action.equals(ACTION_CHECK_VERSION)) {
				handleCheckVersion(params);
				return true;
			}
			if (action.equals(ACTION_GET_CURRENT_LANGUAGE)) {
				JSONObject r = new JSONObject();
				r.put("language", Locale.getDefault().toString());
				callbackContext.success(r);
				return true;
			}
			if (action.equals(ACTION_GET_TOUTIAO_ACCESS_TOKEN_PARAMS)) {
				handleGetTouTiaoAccessTokenParams();
				return true;
			}
			if (action.equals(ACTION_GET_PREFS)) {
				Activity act = cordova.getActivity();
				SharedPreferences pref = act.getSharedPreferences(PREFS_KEY,
						Context.MODE_PRIVATE);
				String value = pref.getString(params, "");
				if (value.equals("")) {
					callbackContext.success("");
				} else {
					JSONObject r = new JSONObject();
					r.put(params, value);
					Log.d("web", "key,value=" + r.toString());
					callbackContext.success(value);
				}
				return true;
			}
			if (action.equals(ACTION_PUT_PREFS)) {
				Activity act = cordova.getActivity();
				SharedPreferences pref = act.getSharedPreferences(PREFS_KEY,
						Context.MODE_PRIVATE);
				JSONObject obj = new JSONObject(params);
				String key = obj.getString("key");
				String value = obj.getString("value");
				Log.d("web", "key,value=" + key + "," + value);
				pref.edit().putString(key, value).commit();
				return true;
			}
			if (action.equals(ACTION_TOAST)) {
				final Activity activity = cordova.getActivity();
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// Toast.makeText(activity, R.string.internet_err,
						// Toast.LENGTH_SHORT).show();
					}
				});
				return true;
			}
		} else
			return true;
		return false;
	}

	private void handleGetTouTiaoAccessTokenParams() {
		try {
			JSONObject jsonObj = new JSONObject();// pet对象，json形式
			Activity act = cordova.getActivity();
			TelephonyManager TelephonyMgr = (TelephonyManager) act
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = TelephonyMgr.getDeviceId();
			if (deviceid == null)
				deviceid = "";
			jsonObj.put("udid", deviceid);
			jsonObj.put("openudid", Secure.getString(act.getContentResolver(),
					Secure.ANDROID_ID));
			jsonObj.put("os", "Android");
			jsonObj.put("mc", getMacAddress(act));
			jsonObj.put("os_version", Build.VERSION.RELEASE);
			jsonObj.put("os_api", Build.VERSION.SDK_INT);
			jsonObj.put("device_model", Build.MANUFACTURER);
			jsonObj.put(
					"resolution",
					act.getResources().getDisplayMetrics().widthPixels
							+ "x"
							+ act.getResources().getDisplayMetrics().heightPixels);
			jsonObj.put("display_density", getDensity(act));
			// jsonObj.put( "carrier" , "" );
			jsonObj.put("language",
					act.getResources().getConfiguration().locale.getLanguage());
			loadUrl("javascript:news.initAccessTokenParams("
					+ jsonObj.toString() + ");");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getDensity(Context context) {
		int dpi = context.getResources().getDisplayMetrics().densityDpi;
		switch (dpi) {
		case DisplayMetrics.DENSITY_LOW:
			return "ldpi";
		case DisplayMetrics.DENSITY_MEDIUM:
			return "mdpi";
		case DisplayMetrics.DENSITY_HIGH:
			return "hdpi";
		case DisplayMetrics.DENSITY_XHIGH:
			return "xhdpi";
		case DisplayMetrics.DENSITY_XXHIGH:
			return "xxhdpi";
		case DisplayMetrics.DENSITY_XXXHIGH:
			return "xxxhdpi";
		default:
			return "mdpi";
		}
	}

	/**
	 * 获取手机mac地址<br/>
	 * 错误返回12个0
	 */
	public static String getMacAddress(Context context) {
		// 获取mac地址：
		String macAddress = "000000000000";
		try {
			WifiManager wifiMgr = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = (null == wifiMgr ? null : wifiMgr
					.getConnectionInfo());
			if (null != info) {
				if (!TextUtils.isEmpty(info.getMacAddress())) {
					macAddress = info.getMacAddress().replace(":", "");
					Log.d("web", "mac:" + macAddress);
				} else
					return macAddress;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return macAddress;
		}
		return macAddress;
	}

	private void handleStartActivity(final String params) {
		cordova.getThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				Intent intent;
				try {
					intent = Intent.parseUri(params, 0);
					// ((Launcher) cordova.getActivity()).startActivity(null,
					// intent, null);
				} catch (Exception e) {
				}
			}
		});
	}

	private void handleSearch(final String params) {
		final Activity activity = cordova.getActivity();
		// if (!NetworkAvailableUtils.isNetworkAvailable(activity)) {
		// activity.runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// Toast.makeText(activity, R.string.internet_err,
		// Toast.LENGTH_SHORT).show();
		// }
		// });
		// return;
		// }
		// String query = params;
		// String itude[] = new String[2];
		// String address = CooeeLocationTool.getInstance(activity).getLocation(
		// itude);
		// if (itude[0] == null || itude[1] == null) {
		// itude[0] = "31.204055632862";
		// itude[1] = "121.41117785465";
		// }
		// Log.v("web", "itude[0] =  " + itude[0] + " itude[1] = " + itude[1]);
		// String curLan = Locale.getDefault().toString();
		// String imsi = CoolMethod.getImsi(activity);
		// StringBuffer sb = new StringBuffer();
		// if (curLan.equals("zh_CN") || curLan.equals("zh_TW")
		// || curLan.equals("zh_HK") || imsi.startsWith("460"))// 国内使用百度地图
		// {
		// sb.append("http://api.map.baidu.com/place/search?")
		// .append("query=").append(query).append("&location=")
		// .append(itude[0]).append(",").append(itude[1])
		// .append("&radius=1000&output=html");
		// } else {
		// sb.append("http://www.google.cn/maps/search/").append(query)
		// .append("/@").append(itude[0]).append(",").append(itude[1])
		// .append(",15z");
		// // sb.append(
		// // "https://maps.googleapis.com/maps/api/place/search/json?"
		// // ).append( "&location=" ).append( itude[0] ).append( "," ).append(
		// // itude[1] ).append( "&radius=" ).append( 1000 )
		// // .append( "&types=" ).append( query ).append( "&keyword="
		// // ).append( query ).append( "&language=" ).append( "en" ).append(
		// // "&sensor=true&key=" )
		// // .append( "AIzaSyBzAclzC3NPT61rjUrr7DJtNO-ZNS4VbB0&output=html" );
		// }
		// Log.v("web", "uri " + sb.toString());
		// Intent intent = new Intent(Intent.ACTION_VIEW);
		// Uri uri = Uri.parse(sb.toString());
		// intent.setData(uri);
		// ((Launcher) cordova.getActivity()).startActivity(null, intent, null);
	}

	private void handleStartUrl(final String params) {
		final Activity activity = cordova.getActivity();
		// if (!NetworkAvailableUtils.isNetworkAvailable(activity)) {
		// activity.runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// Toast.makeText(activity, R.string.internet_err,
		// Toast.LENGTH_SHORT).show();
		// }
		// });
		// return;
		// }
		// Intent intent = new Intent(Intent.ACTION_VIEW);
		// Uri uri = Uri.parse(params.toString());
		// intent.setData(uri);
		// ((Launcher) cordova.getActivity()).startActivity(null, intent, null);
	}

	public static final String urls[] = new String[] { "192.168.1.222:85",
			"192.168.1.222:86", "192.168.1.222:87", "192.168.1.222:88" };

	public static final String getDataServerUrl() {
		return "http://" + urls[new Random().nextInt(urls.length)] /*
																	 * +
																	 * "services"
																	 */
				+ "/iloong/pui/ServicesEngineV1/DataService";
	}

	private void handleCheckVersion(final String serverUrl) {
		cordova.getThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				JSONObject reqJson = null;
				try {
					reqJson = JsonUtil.NewRequestJSON(cordova.getActivity(), 4,
							"uiupdate");
					reqJson.put("Action", "3006");
					reqJson.put("p1", 0);
					reqJson.put("p2", 0);// 1:用户主动更新，0：后台自动更新
					reqJson.put("p3", Locale.getDefault().toString());
					reqJson.put("p4", 0);
					Log.v("web", "proxy req:" + reqJson.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (reqJson == null)
					return;
				int i = 3;
				while (i > 0) {
					ResultEntity result = CoolHttpClient.postEntity(
							getDataServerUrl(), reqJson.toString());
					if (result.exception != null) {
						Log.v("web", "proxy rsp:(error)" + result.httpCode
								+ " " + result.exception);
						i--;
						continue;
					}
					Log.v("web", "proxy rsp:" + result.httpCode + " "
							+ result.content);
					JSONObject resJson = null;
					try {
						resJson = new JSONObject(result.content);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (resJson == null)
						return;
					int retcode = resJson.optInt("retcode");
					Log.v("web", "proxy resJson retcode=" + retcode);
					if (retcode == 0) {
						String h5 = resJson.optString("h5");
						if (h5 != null && !h5.equals("")) {
							// 取出回应字串
							Log.v("web", "proxy resJson h5=" + h5);
							// h5 = "{"+"\"retcode\" : \"0\","
							// + "\"h5version\" : \"2\","
							// +
							// "\"h5url\" : \"http://192.168.0.178/test_ionic/fav.zip\""
							// +"}";
							loadUrl("javascript:onCheckVersionSuccess(" + h5
									+ ");");
						}
					}
					return;
				}
			}
		});
	}

	private void handleDownloadPlugin(final String serverUrl) {
		cordova.getThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				// download
				int i = 3;
				File file = null;
				while (i > 0) {
					file = downloadFile(serverUrl);
					if (file != null)
						break;
					i--;
				}
				// unzip
				if (file == null)
					return;
				final String unzipDir = file.getAbsolutePath() + "_unzip/";
				boolean res = Unzip(file.getAbsolutePath(), unzipDir);
				deleteFile(file);
				if (!res) {
					deleteFile(new File(unzipDir));
					return;
				}
				// update
				final Activity activity = null;// = LauncherAppState
				// .getActivityInstance();
				if (activity != null) {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (isProxyValid(unzipDir, activity)) {
								File dir = new File(
										PluginProxyManager.DIR_PROXY);
								if (deleteFile(dir)
										&& new File(unzipDir).renameTo(dir)) {
									String url = PluginProxyManager
											.getInstance()
											.loadProxy(
													activity,
													(WebView) LauncherPlugin.this.webView
															.getView());
									// update
									loadUrl(url);
								}
							}
							deleteFile(new File(unzipDir));
						}
					});
				}
			}
		});
	}

	public boolean isProxyValid(String proxyDir, Activity activity) {
		File jarFile = new File(proxyDir + PluginProxyManager.JAR_NAME);
		if (jarFile.exists()) {
			DexClassLoader cl = new DexClassLoader(jarFile.toString(),
					proxyDir, null, activity.getClassLoader());
			Class<?> c;
			try {
				c = cl.loadClass("com.cooee.cordova.plugin.PluginProxy");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		String url = proxyDir + PluginProxyManager.URL_NAME;
		File urlFile = new File(url);
		if (urlFile.exists()) {
			return true;
		}
		return false;
	}

	private void loadUrl(final String s) {
		this.cordova.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LauncherPlugin.this.webView.loadUrlIntoView(s, true);
			}
		});
	}

	public static boolean deleteFile(File file) {
		if (!file.exists())
			return true;
		if (file.isFile())
			return file.delete();
		else {
			boolean res = true;
			File[] files = file.listFiles();
			if (files != null) {
				for (File tmp : files) {
					res &= deleteFile(tmp);
				}
			}
			res &= file.delete();
			return res;
		}
	}

	public static File downloadFile(String serverUrl) {
		String dirPath = PluginProxyManager.DIR_ROOT;
		File dir = new File(dirPath);
		dir.mkdirs();
		File file = new File(dirPath + System.currentTimeMillis());
		// 如果目标文件已经存在，则删除。产生覆盖旧文件的效果
		if (file.exists()) {
			file.delete();
		}
		try {
			// 构造URL
			URL url = new URL(serverUrl);
			// 打开连接
			URLConnection con = url.openConnection();
			// 获得文件的长度
			int contentLength = con.getContentLength();
			Log.d("web", "download contentLength=" + contentLength);
			// 输入流
			InputStream is = con.getInputStream();
			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			OutputStream os = new FileOutputStream(file);
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			// 完毕，关闭所有链接
			os.close();
			is.close();
			Log.d("web", "download fileLength=" + file.length());
			if (file.length() != contentLength) {
				if (file.exists()) {
					file.delete();
				}
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (file.exists()) {
				file.delete();
			}
			return null;
		}
		return file;
	}

	public static boolean Unzip(String zipFile, String targetDir) {
		int BUFFER = 4096; // 这里缓冲区我们使用4KB，
		String strEntry; // 保存每个zip的条目名称
		try {
			BufferedOutputStream dest = null; // 缓冲输出流
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry; // 每个zip条目的实例
			int readedBytes = 0;
			byte[] buf = new byte[1024];
			while ((entry = zis.getNextEntry()) != null) {
				File file = new File(targetDir + entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					// 如果指定文件的目录不存在,则创建之.
					File parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}
					FileOutputStream fileOut = new FileOutputStream(file);
					// fileOut = new FileOutputStream(file); 此方法存放到该项目目录下
					while ((readedBytes = zis.read(buf)) > 0) {
						fileOut.write(buf, 0, readedBytes);
					}
					fileOut.close();
				}
			}
			zis.close();
		} catch (Exception cwj) {
			cwj.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Called when the system is about to start resuming a previous activity.
	 * 
	 * @param multitasking
	 *            Flag indicating if multitasking is turned on for app
	 */
	public void onPause(boolean multitasking) {
		PluginProxyManager.getInstance().onPause(multitasking);
	}

	/**
	 * Called when the activity will start interacting with the user.
	 * 
	 * @param multitasking
	 *            Flag indicating if multitasking is turned on for app
	 */
	public void onResume(boolean multitasking) {
		PluginProxyManager.getInstance().onResume(multitasking);
	}

	/**
	 * Called when the activity is becoming visible to the user.
	 */
	public void onStart() {
		PluginProxyManager.getInstance().onStart();
	}

	/**
	 * Called when the activity is no longer visible to the user.
	 */
	public void onStop() {
		PluginProxyManager.getInstance().onStop();
	}

	/**
	 * Called when the activity receives a new intent.
	 */
	public void onNewIntent(Intent intent) {
		PluginProxyManager.getInstance().onNewIntent(intent);
	}

	/**
	 * The final call you receive before your activity is destroyed.
	 */
	public void onDestroy() {
		PluginProxyManager.getInstance().onDestroy();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		PluginProxyManager.getInstance().onActivityResult(requestCode,
				resultCode, intent);
	}

	public Boolean shouldAllowRequest(String url) {
		return PluginProxyManager.getInstance().shouldAllowRequest(url);
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
		return PluginProxyManager.getInstance().shouldAllowNavigation(url);
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
		return PluginProxyManager.getInstance().shouldOpenExternalUrl(url);
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
		return PluginProxyManager.getInstance().onOverrideUrlLoading(url);
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
		return PluginProxyManager.getInstance().remapUri(uri);
	}

	/**
	 * Called by the system when the device configuration changes while your
	 * activity is running.
	 * 
	 * @param newConfig
	 *            The new device configuration
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		PluginProxyManager.getInstance().onConfigurationChanged(newConfig);
	}
}
