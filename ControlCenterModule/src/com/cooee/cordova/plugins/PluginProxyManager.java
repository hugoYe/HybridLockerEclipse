package com.cooee.cordova.plugins;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import dalvik.system.DexClassLoader;

public class PluginProxyManager {

	public static String DIR_ROOT = "h5/";
	public static String DIR_PROXY = "proxy/";
	public static final String URL_NAME = "www/index.html";
	public static final String JAR_NAME = "www/jar/proxy.jar";
	private static PluginProxyManager instance;
	private IPluginProxy proxy;
	private String url;

	public static PluginProxyManager getInstance() {
		if (instance == null) {
			synchronized (PluginProxyManager.class) {
				if (instance == null) {
					instance = new PluginProxyManager();
				}
			}
		}
		return instance;
	}

	public PluginProxyManager() {
		// Context context = LauncherAppState.getInstance().getContext();
		// DIR_ROOT = context.getFilesDir().getPath() + "/" + DIR_ROOT;
		// DIR_PROXY = DIR_ROOT + "proxy/";
		// Log.d("web", "rootPath=" + DIR_ROOT);
	}

	public String loadProxy(Context context, WebView v) {
		// DIR_ROOT = context.getFilesDir().getPath() + "/" + DIR_ROOT;
		// DIR_PROXY = DIR_ROOT + "proxy/";
		//
		// File jarFile = new File(DIR_PROXY + JAR_NAME);
		String path = context.getFilesDir().getPath() + "/www/proxydex.jar";
		DIR_PROXY = context.getFilesDir().getPath() + "/www/";
		File jarFile = new File(path);

		if (jarFile.exists()) {
			DexClassLoader cl = new DexClassLoader(jarFile.toString(),
					DIR_PROXY, null, context.getClassLoader());
			Class<?> c;
			try {
				c = cl.loadClass("com.cooee.cordova.plugins.PluginProxy");
				IPluginProxy proxy = (IPluginProxy) c.newInstance();
				if (proxy != null) {
					proxy.init(context, v);
					this.proxy = proxy;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String url = DIR_PROXY + URL_NAME;
		File urlFile = new File(url);
		if (urlFile.exists()) {
			this.url = "file://" + url;
			return this.url;
		}
		return null;
	}

	public void onPageFinishedLoading() {
		if (proxy != null)
			proxy.onPageFinishedLoading();
		Log.d("web", "proxy onPageFinishedLoading");
	}

	public int execute(Intent intent, String title, Bitmap icon) {
		if (proxy != null)
			return proxy.execute(intent, title, icon);
		return -1;
	}

	public void onPause(boolean multitasking) {
		if (proxy != null)
			proxy.onPause(multitasking);
		Log.d("web", "proxy onPause");
	}

	public void onResume(boolean multitasking) {
		if (proxy != null)
			proxy.onResume(multitasking);
		Log.d("web", "proxy onResume");
	}

	public void onStart() {
		if (proxy != null)
			proxy.onStart();
		Log.d("web", "proxy onStart");
	}

	public void onStop() {
		if (proxy != null)
			proxy.onStop();
		Log.d("web", "proxy onStop");
	}

	public void onNewIntent(Intent intent) {
		if (proxy != null)
			proxy.onNewIntent(intent);
		Log.d("web", "proxy onNewIntent");
	}

	public void onDestroy() {
		if (proxy != null)
			proxy.onDestroy();
		Log.d("web", "proxy onDestroy");
	}

	public void onLauncherLoadFinish() {
		if (proxy != null)
			proxy.onLauncherLoadFinish();
		Log.d("web", "proxy onLauncherLoadFinish");
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (proxy != null)
			proxy.onActivityResult(requestCode, resultCode, intent);
		Log.d("web", "proxy onActivityResult");
	}

	public Boolean shouldAllowRequest(String url2) {
		if (proxy != null)
			return proxy.shouldAllowRequest(url2);
		Log.d("web", "proxy shouldAllowRequest");
		return null;
	}

	public Boolean shouldAllowNavigation(String url2) {
		if (proxy != null)
			return proxy.shouldAllowNavigation(url2);
		Log.d("web", "proxy shouldAllowNavigation");
		return null;
	}

	public Boolean shouldOpenExternalUrl(String url2) {
		if (proxy != null)
			return proxy.shouldOpenExternalUrl(url2);
		Log.d("web", "proxy shouldOpenExternalUrl");
		return null;
	}

	public boolean onOverrideUrlLoading(String url2) {
		if (proxy != null)
			return proxy.onOverrideUrlLoading(url2);
		Log.d("web", "proxy onOverrideUrlLoading");
		return false;
	}

	public Uri remapUri(Uri uri) {
		if (proxy != null)
			return proxy.remapUri(uri);
		Log.d("web", "proxy remapUri");
		return null;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		if (proxy != null)
			proxy.onConfigurationChanged(newConfig);
		Log.d("web", "proxy onConfigurationChanged");
	}
}
