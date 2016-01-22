package com.cooeelock.core.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;
import dalvik.system.DexClassLoader;

final public class JarPluginProxyManager implements IPluginProxy {

	private final String TAG = "JarPluginProxyManager";

	public static final String PLUGINS_PACKAGE_NAME = "com.cooeeui.lock.jar.plugins";
	private final String PLUGINS_CLASS_PROXY_NAME = "com.cooeelock.core.PluginProxy";

	private String DIR_ROOT = "h5/";
	private String DIR_PROXY = "proxy/";
	private String JAR_NAME = "proxydex.jar";

	private static JarPluginProxyManager instance;

	private boolean mLoadSuccessed;
	private Context mContext;

	private Object obj;
	private Class<?> mProxyClass;
	private Constructor<?> mProxyConstructor;
	private Method methodCopyFinish;
	private Method methodExecute;
	private Method methodOnPause;
	private Method methodOnResume;
	private Method methodOnStart;
	private Method methodOnStop;
	private Method methodOnNewIntent;
	private Method methodOnDestroy;
	private Method methodOnLauncherLoadFinish;
	private Method methodOnActivityResult;
	private Method methodShouldAllowRequest;
	private Method methodShouldAllowNavigation;
	private Method methodShouldOpenExternalUrl;
	private Method methodOnOverrideUrlLoading;
	private Method methodRemapUri;
	private Method methodOnConfigurationChanged;
	private Method methodOnPageFinishedLoading;

	public static JarPluginProxyManager getInstance() {
		if (instance == null) {
			synchronized (JarPluginProxyManager.class) {
				if (instance == null) {
					instance = new JarPluginProxyManager();
				}
			}
		}
		return instance;
	}

	public JarPluginProxyManager() {
	}

	private boolean loadClassType() {
		try {
			DIR_ROOT = "h5/";
			DIR_PROXY = "proxy/";

			DIR_ROOT = mContext.getFilesDir().getPath() + "/" + DIR_ROOT;
			DIR_PROXY = DIR_ROOT + DIR_PROXY;

			File jarFile = new File(DIR_PROXY + JAR_NAME);

			Log.e(TAG, "######## jarFilePath = " + jarFile.getAbsolutePath());

			if (jarFile.exists()) {

				Log.e(TAG, "######## loadClassType 111");

				DexClassLoader cl = new DexClassLoader(jarFile.toString(),
						DIR_PROXY, null, mContext.getClassLoader());

				Log.e(TAG, "######## loadClassType 222");

				mProxyClass = cl.loadClass(PLUGINS_CLASS_PROXY_NAME);

				Log.e(TAG, "######## loadClassType 333");
				mProxyConstructor = mProxyClass
						.getDeclaredConstructor(Context.class);
				methodCopyFinish = mProxyClass.getDeclaredMethod("copyFinish",
						Boolean.class);
				methodExecute = mProxyClass.getDeclaredMethod("execute",
						String.class, JSONArray.class);
				methodOnPause = mProxyClass.getDeclaredMethod("onPause",
						Boolean.class);
				methodOnResume = mProxyClass.getDeclaredMethod("onResume",
						Boolean.class);
				methodOnStart = mProxyClass.getDeclaredMethod("onStart");
				methodOnStop = mProxyClass.getDeclaredMethod("onStop");
				methodOnNewIntent = mProxyClass.getDeclaredMethod(
						"onNewIntent", Intent.class);
				methodOnDestroy = mProxyClass.getDeclaredMethod("onDestroy");
				methodOnLauncherLoadFinish = mProxyClass
						.getDeclaredMethod("onLauncherLoadFinish");
				methodOnActivityResult = mProxyClass.getDeclaredMethod(
						"onActivityResult", Integer.class, Integer.class,
						Intent.class);
				methodShouldAllowRequest = mProxyClass.getDeclaredMethod(
						"shouldAllowRequest", String.class);
				methodShouldAllowNavigation = mProxyClass.getDeclaredMethod(
						"shouldAllowNavigation", String.class);
				methodShouldOpenExternalUrl = mProxyClass.getDeclaredMethod(
						"shouldOpenExternalUrl", String.class);
				methodOnOverrideUrlLoading = mProxyClass.getDeclaredMethod(
						"onOverrideUrlLoading", String.class);
				methodRemapUri = mProxyClass.getDeclaredMethod("remapUri",
						Uri.class);
				methodOnConfigurationChanged = mProxyClass.getDeclaredMethod(
						"onConfigurationChanged", Configuration.class);
				methodOnPageFinishedLoading = mProxyClass
						.getDeclaredMethod("onPageFinishedLoading");

				Log.e(TAG, "######## loadClassType success !!");
				return true;
			} else {
				Log.e(TAG, "######## jarFile is not exist !");
				return false;
			}

		} catch (ClassNotFoundException e) {
			Log.e(TAG, "######## loadClassType error = " + e);
			e.printStackTrace();
			return false;
		} catch (SecurityException e) {
			Log.e(TAG, "######## loadClassType error = " + e);
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "######## loadClassType error = " + e);
			e.printStackTrace();
			return false;
		}
	}

	private boolean loadInstance() {
		try {
			obj = mProxyConstructor.newInstance(mContext);
			Log.e(TAG, "######## loadInstance success = " + obj);
			return true;
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "######## loadInstance error = " + e);
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			Log.e(TAG, "######## loadInstance error = " + e);
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			Log.e(TAG, "######## loadInstance error = " + e);
			e.printStackTrace();
			return false;
		} catch (InvocationTargetException e) {
			Log.e(TAG, "######## loadInstance error = " + e);
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean loadProxy(Context context) {

		if (mLoadSuccessed) {
			Log.i(TAG, "######## proxy had loaded successed !!!");
			return true;
		}

		if (context == null) {
			Log.e(TAG, "######## remoteContext is null !");
			return false;
		}

		mContext = context;
		Log.i(TAG, "######## mContext = " + mContext);

		if (loadClassType() && loadInstance()) {
			Log.e(TAG, "######## loadProxy success !");
			mLoadSuccessed = true;
			return true;
		}

		return false;

	}

	@Override
	public int execute(String action, final JSONArray args) {
		// TODO Auto-generated method stub
		if (methodExecute != null) {
			try {
				methodExecute.invoke(obj, action, args);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return -1;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return -1;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return -1;
			}
		}
		return -1;
	}

	public void copyFinish(Boolean multitasking){
		if (methodCopyFinish != null) {
			try {
				methodCopyFinish.invoke(obj, multitasking);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onPause(Boolean multitasking) {
		// TODO Auto-generated method stub
		if (methodOnPause != null) {
			try {
				methodOnPause.invoke(obj, multitasking);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onResume(Boolean multitasking) {
		// TODO Auto-generated method stub
		if (methodOnResume != null) {
			try {
				methodOnResume.invoke(obj, multitasking);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		if (methodOnStart != null) {
			try {
				methodOnStart.invoke(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		if (methodOnStop != null) {
			try {
				methodOnStop.invoke(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		if (methodOnNewIntent != null) {
			try {
				methodOnNewIntent.invoke(obj, intent);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (methodOnDestroy != null) {
			try {
				methodOnDestroy.invoke(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onLauncherLoadFinish() {
		// TODO Auto-generated method stub
		if (methodOnLauncherLoadFinish != null) {
			try {
				methodOnLauncherLoadFinish.invoke(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onActivityResult(Integer requestCode, Integer resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		if (methodOnActivityResult != null) {
			try {
				methodOnActivityResult.invoke(obj, requestCode, requestCode,
						intent);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Boolean shouldAllowRequest(String url) {
		// TODO Auto-generated method stub
		if (methodShouldAllowRequest != null) {
			try {
				methodShouldAllowRequest.invoke(obj, url);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public Boolean shouldAllowNavigation(String url) {
		// TODO Auto-generated method stub
		if (methodShouldAllowNavigation != null) {
			try {
				methodShouldAllowNavigation.invoke(obj, url);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public Boolean shouldOpenExternalUrl(String url) {
		// TODO Auto-generated method stub
		if (methodShouldOpenExternalUrl != null) {
			try {
				methodShouldOpenExternalUrl.invoke(obj, url);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public boolean onOverrideUrlLoading(String url) {
		// TODO Auto-generated method stub
		if (methodOnOverrideUrlLoading != null) {
			try {
				methodOnOverrideUrlLoading.invoke(obj, url);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	@Override
	public Uri remapUri(Uri uri) {
		// TODO Auto-generated method stub
		if (methodRemapUri != null) {
			try {
				methodRemapUri.invoke(obj, uri);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		if (methodOnConfigurationChanged != null) {
			try {
				methodOnConfigurationChanged.invoke(obj, newConfig);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onPageFinishedLoading() {
		// TODO Auto-generated method stub
		if (methodOnPageFinishedLoading != null) {
			try {
				methodOnPageFinishedLoading.invoke(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

}
