package com.cooeelock.core.plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class PluginProxyManager {

	private final String TAG = "PluginProxyManager";

	private String DIR_ROOT = "h5/";
	private String DIR_PROXY = "proxy/";
	private final String JAR_NAME = "proxydex.jar";
	private static PluginProxyManager instance;
	private IPluginProxy proxy;

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
	}

	private Object obj;
	private Method methodInit;
	private Method methodExecute;

	public boolean loadProxy(Context context) {

		DIR_ROOT = context.getFilesDir().getPath() + "/" + DIR_ROOT;
		DIR_PROXY = DIR_ROOT + DIR_PROXY;

		File jarFile = new File(DIR_PROXY + JAR_NAME);
		Log.e(TAG, "######### jarFile.path = " + jarFile.getAbsolutePath());

		if (jarFile.exists()) {
			Log.e(TAG, "######### loadProxy begin");

			DexClassLoader cl = new DexClassLoader(jarFile.toString(),
					DIR_PROXY, null, context.getClassLoader());
			Class<?> c;
			try {
				c = cl.loadClass("com.cooeelock.core.plugin.PluginProxy");
				obj = c.newInstance();
				if (obj != null) {
					methodInit = c.getDeclaredMethod("init", Context.class);
					methodInit.invoke(obj, context);
					methodExecute = c.getDeclaredMethod("execute",
							String.class, JSONArray.class);
					Log.e(TAG, "######### loadProxy success !!!");
					return true;
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
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * 加载插件jar包，jar包路径data/data/锁屏包名/files/h5/proxy/proxydex.jar
	 * */
	// public boolean loadProxy(Context context) {
	//
	// DIR_ROOT = context.getFilesDir().getPath() + "/" + DIR_ROOT;
	// DIR_PROXY = DIR_ROOT + DIR_PROXY;
	//
	// File jarFile = new File(DIR_PROXY + JAR_NAME);
	// Log.e(TAG, "######### jarFile.path = " + jarFile.getAbsolutePath());
	//
	// if (jarFile.exists()) {
	// Log.e(TAG, "######### loadProxy begin");
	//
	// DexClassLoader cl = new DexClassLoader(jarFile.toString(),
	// DIR_PROXY, null, context.getClassLoader());
	// Class<?> c;
	// try {
	// c = cl.loadClass("com.cooeelock.core.plugin.PluginProxy");
	// IPluginProxy proxy = (IPluginProxy) c.newInstance();
	// if (proxy != null) {
	// proxy.init(context);
	// this.proxy = proxy;
	//
	// Log.e(TAG, "######### loadProxy success !!!");
	// return true;
	// }
	// } catch (ClassNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (InstantiationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// return false;
	// }

	public void onPageFinishedLoading() {
		if (proxy != null)
			proxy.onPageFinishedLoading();
		Log.d(TAG, "proxy onPageFinishedLoading");
	}

	public int execute(String action, final JSONArray args) {
		Log.e(TAG, "######### execute");
		if (methodExecute != null) {
			try {
				methodExecute.invoke(obj, action, args);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -1;
	}

	// public int execute(String action, final JSONArray args) {
	// Log.e(TAG, "######### execute");
	// if (proxy != null)
	// return proxy.execute(action, args);
	// return -1;
	// }

	public void onPause(boolean multitasking) {
		if (proxy != null)
			proxy.onPause(multitasking);
		Log.d(TAG, "proxy onPause");
	}

	public void onResume(boolean multitasking) {
		if (proxy != null)
			proxy.onResume(multitasking);
		Log.d(TAG, "proxy onResume");
	}

	public void onStart() {
		if (proxy != null)
			proxy.onStart();
		Log.d(TAG, "proxy onStart");
	}

	public void onStop() {
		if (proxy != null)
			proxy.onStop();
		Log.d(TAG, "proxy onStop");
	}

	public void onNewIntent(Intent intent) {
		if (proxy != null)
			proxy.onNewIntent(intent);
		Log.d(TAG, "proxy onNewIntent");
	}

	public void onDestroy() {
		if (proxy != null)
			proxy.onDestroy();
		Log.d(TAG, "proxy onDestroy");
	}

	public void onLauncherLoadFinish() {
		if (proxy != null)
			proxy.onLauncherLoadFinish();
		Log.d(TAG, "proxy onLauncherLoadFinish");
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (proxy != null)
			proxy.onActivityResult(requestCode, resultCode, intent);
		Log.d(TAG, "proxy onActivityResult");
	}

	public Boolean shouldAllowRequest(String url2) {
		if (proxy != null)
			return proxy.shouldAllowRequest(url2);
		Log.d(TAG, "proxy shouldAllowRequest");
		return null;
	}

	public Boolean shouldAllowNavigation(String url2) {
		if (proxy != null)
			return proxy.shouldAllowNavigation(url2);
		Log.d(TAG, "proxy shouldAllowNavigation");
		return null;
	}

	public Boolean shouldOpenExternalUrl(String url2) {
		if (proxy != null)
			return proxy.shouldOpenExternalUrl(url2);
		Log.d(TAG, "proxy shouldOpenExternalUrl");
		return null;
	}

	public boolean onOverrideUrlLoading(String url2) {
		if (proxy != null)
			return proxy.onOverrideUrlLoading(url2);
		Log.d(TAG, "proxy onOverrideUrlLoading");
		return false;
	}

	public Uri remapUri(Uri uri) {
		if (proxy != null)
			return proxy.remapUri(uri);
		Log.d(TAG, "proxy remapUri");
		return null;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		if (proxy != null)
			proxy.onConfigurationChanged(newConfig);
		Log.d(TAG, "proxy onConfigurationChanged");
	}
}
