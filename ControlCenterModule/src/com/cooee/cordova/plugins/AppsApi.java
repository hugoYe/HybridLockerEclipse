package com.cooee.cordova.plugins;

import java.util.ArrayList;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.coco.lock.favorites.AppInfo;
import com.coco.lock.favorites.FavoritesModel;
import com.coco.lock.favorites.api.FavoritesApi;
import com.cooee.control.center.module.R;
import com.cooee.control.center.module.api.LockWrapApi;
import com.cooee.control.center.module.base.Tools;

public class AppsApi extends CordovaPlugin {

	private static final String TAG = "AppsApiPlugin";

	public final String ACTION_CHECK_AVAILABILITY = "checkAvailability";
	public final String ACTION_START_ACTIVITY = "startActivity";
	public final String ACTION_START_SHORTCUT = "startShortcut";
	public final String ACTION_START_BROWSER_URL = "startUrl";
	public final String ACTION_BIND_FAVORITE_APP = "bindFavoriteApp";
	public final String ACTION_RESET_LIGHT = "resetLight";

	private CallbackContext mCallbackContext;

	private static UnlockListener sUnlockListener;
	
	private Context mContext;
	FavoritesApi mFavorites = null;
	
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
		
		//注册常用数据库读取完成广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(FavoritesModel.ACTION_LOAD_FAVOTITE_SUCCESS);
		mContext.registerReceiver(mFavoriteReceiver, filter);
		
		//获取常用信息的提供类
		mFavorites = new FavoritesApi(mContext);
		mFavorites.init();

	}
	
	@Override
	public boolean execute(String action, final JSONArray args,
			CallbackContext callbackContext) throws JSONException {

		mCallbackContext = callbackContext;
		if (action.equals(ACTION_RESET_LIGHT)) {
			LockWrapApi.resetLight();
		}
		if (action.equals(ACTION_CHECK_AVAILABILITY)) {
			String uri = args.getString(0);
			this.checkAvailability(uri, callbackContext);
			return true;
		} else if (action.equals(ACTION_START_ACTIVITY)) {
			startApp(args);
			return true;
		} else if (action.equals(ACTION_START_SHORTCUT)) {
			startShortcut(args);
			return true;
		} else if (action.equals(ACTION_START_BROWSER_URL)) {
			startUrl(args);
			return true;
		} else if (action.equals(ACTION_BIND_FAVORITE_APP)) {
			bindWebFavoriteApp();
			return true;
		}
		return false;
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "AppsApi onDestroy");
		if( mFavoriteReceiver != null )
		{
			mContext.unregisterReceiver( mFavoriteReceiver );
			mFavoriteReceiver = null;
		}
		if (mFavorites != null){
			mFavorites.onDestroy();
		}
		super.onDestroy();
	}

	// Thanks to
	// http://floresosvaldo.com/android-cordova-plugin-checking-if-an-app-exists
	public boolean appInstalled(String uri) {
		Context ctx = null;
		if (this.cordova.getActivity() != null) {
			ctx = this.cordova.getActivity().getApplicationContext();
		} else if (this.cordova.getContext() != null) {
			ctx = this.cordova.getContext();
		}
		final PackageManager pm = ctx.getPackageManager();
		boolean app_installed = false;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			app_installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed;
	}

	private void startApp(final JSONArray args) {
		Log.e(TAG, "######## startActivity 111");
		if (cordova.getActivity() != null) {
			Log.e(TAG, "######## startActivity 222");
			cordova.getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Log.e(TAG, "######## startActivity 333");
					try {

						String method = args.getString(0);
						Log.d("", "intent=" + method);
						Intent intent;
						try {
							intent = Intent.parseUri(method, 0);
						} catch (Exception e) {
							intent = new Intent(Intent.ACTION_VIEW);
							Uri uri = Uri.parse(method);
							intent.setData(uri);
						}
						startActivitySafely(cordova.getActivity(), intent);
						if (sUnlockListener != null) {
							sUnlockListener.onUnlock();
						}
					} catch (JSONException ex) {
						mCallbackContext.sendPluginResult(new PluginResult(
								PluginResult.Status.JSON_EXCEPTION));
					}
				}

			});
		} else if (cordova.getContext() != null) {
			Log.e(TAG, "######## startActivity 444, cordova.getContext() = "
					+ cordova.getContext());
			cordova.getCordovaWrap().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Log.e(TAG, "######## startActivity 555");
					try {

						String method = args.getString(0);
						Log.i(TAG, "######## startActivity--- method = "
								+ method);
						Intent intent;
						try {
							intent = Intent.parseUri(method, 0);
							Log.i(TAG, "######## startActivity--- intent = "
									+ intent.toString());

						} catch (Exception e) {
							intent = new Intent(Intent.ACTION_VIEW);
							Uri uri = Uri.parse(method);
							intent.setData(uri);
						}
						startActivitySafely(cordova.getContext(), intent);
						if (sUnlockListener != null) {
							sUnlockListener.onUnlock();
						}
					} catch (JSONException ex) {
						mCallbackContext.sendPluginResult(new PluginResult(
								PluginResult.Status.JSON_EXCEPTION));
					}
				}

			});
		}
	}

	/**
	 * js中用来启动配置的应用程序
	 * 
	 * @param intentUri
	 *            [String] (args.getString(0)) : 需要启动的intent ---> example:
	 *            "#Intent;action=android.intent.action.MAIN;category=android.intent.category.LAUNCHER;launchFlags=0x10000000;component=com.coco.lock2.app.Pee/.InsideLockActivity;end"
	 * @param appId
	 *            [long] (args.getString(1)) : 需要配置的appid(微入口所需要配置的参数) --->
	 *            example : 10009
	 * @param createShortcut
	 *            [boolean] (args.getString(2)) : 是否需要创建桌面快捷方式
	 *            ，配合title及imgBase64使用
	 * @param title
	 *            [String] (args.getString(3)) : 创建桌面快捷方式的名称 ---> example: "百度"
	 * @param imgBase64
	 *            [String] (args.getString(4)) : 创建桌面快捷方式的图标 ---> example:
	 *            "data:image/gif;base64, werfjls.."
	 * 
	 */
	private void startShortcut(final JSONArray args) {

		if (cordova.getActivity() != null) {
			cordova.getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					startShortcutImpl(args, cordova.getActivity());
				}

			});
		} else if (cordova.getContext() != null) {

			cordova.getCordovaWrap().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					startShortcutImpl(args, cordova.getContext());
				}

			});
		}
	}

	private void startShortcutImpl(final JSONArray args, Context context) {

		try {
			String intentUri = args.getString(0);
			long appid = args.getLong(1);
			boolean createShortcut = args.getBoolean(2);

			Intent intent = null;
			try {
				intent = Intent.parseUri(intentUri, 0);
				intent.putExtra("APP_ID", appid);

			} catch (Exception e) {
				intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse(intentUri);
				intent.setData(uri);
				e.printStackTrace();
			}

			if (createShortcut) {
				String title = args.getString(3);
				String base64String = args.getString(4);
				Bitmap icon = null;
				if (base64String.split(",").length > 1) {
					icon = Tools.base64ToBitmap(base64String.split(",")[1]);
				}
				if (context != null) {
					Log.e(TAG, "######## createAppShortCut");
					createSystemShortCut(context, intent, title, icon);
				}
			}

			if (context != null) {
				startActivitySafely(context, intent);
			}

			if (sUnlockListener != null) {
				sUnlockListener.onUnlock();
			}

		} catch (JSONException ex) {
			mCallbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.JSON_EXCEPTION));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * js中用来启动url链接的接口, args中传过来的有如下四个参数
	 * 
	 * @param url
	 *            [String] (args.getString(0)) : 需要启动的链接 ---> example:
	 *            "http://m.baidu.com"
	 * @param createShortcut
	 *            [boolean] ( args.getBoolean(1)) : 是否需要创建桌面快捷方式
	 *            ，配合title及imgBase64使用
	 * @param title
	 *            [String] (args.getString(2)) : 创建桌面快捷方式的名称 ---> example: "百度"
	 * @param imgBase64
	 *            [String] (args.getString(3)) : 创建桌面快捷方式的图标 ---> example:
	 *            "data:image/gif;base64, werfjls.."
	 * 
	 */
	private void startUrl(final JSONArray args) {

		if (cordova.getActivity() != null) {

			cordova.getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					startUrlImpl(args, cordova.getActivity());
				}

			});
		} else if (cordova.getContext() != null) {

			cordova.getCordovaWrap().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					startUrlImpl(args, cordova.getContext());
				}

			});
		}
	}

	private void startUrlImpl(final JSONArray args, Context context) {

		try {
			String url = args.getString(0);
			boolean createShortcut = args.getBoolean(1);

			Intent intent = null;
			try {
				intent = new Intent(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setFlags((intent.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				Uri uri = Uri.parse(url);
				intent.setData(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (createShortcut) {
				String title = args.getString(2);
				String imgBase64 = args.getString(3);
				Bitmap icon = null;
				if (imgBase64.split(",").length > 1) {
					icon = Tools.base64ToBitmap(imgBase64.split(",")[1]);
				}

				if (context != null) {
					createSystemShortCut(context, intent, title, icon);
				}
			}

			if (context != null) {
				if (url.startsWith("https://play.google.com/store/apps/")
						|| url.startsWith("market")) {
					openGooglePlay(context, intent);
				} else {
					startActivitySafely(context, intent);
				}
			}

			if (sUnlockListener != null) {
				sUnlockListener.onUnlock();
			}
		} catch (JSONException ex) {
			mCallbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.JSON_EXCEPTION));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkAvailability(String uri, CallbackContext callbackContext) {
		if (appInstalled(uri)) {
			callbackContext.success();
		} else {
			callbackContext.error("");
		}
	}

	public void bindWebFavoriteApp() {
		cordova.getThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				Context ctx = null;
				if (cordova.getActivity() != null) {
					ctx = cordova.getActivity();
				} else if (cordova.getContext() != null) {
					ctx = cordova.getContext();
				}

				final JSONObject object = new JSONObject();// 创建一个总的对象，这个对象对整个json串
				JSONArray jsonarray = new JSONArray();// json数组，里面包含的内容为pet的所有对象
				Log.i(TAG, "######## bindWebFavoriteApp---context = " + ctx);
				ArrayList<AppInfo> list = mFavorites.getFavoriteApp();
				Log.i(TAG,
						"######## bindWebFavoriteApp,list.size = "
								+ list.size());
				for (int i = 0; i < list.size(); i++) {
					AppInfo app = list.get(i);
					try {
						JSONObject jsonObj = new JSONObject();// pet对象，json形式
						Log.i(TAG,
								"######## bindWebFavoriteApp--- app.appName = "
										+ app.appName + ", app.appIntent = "
										+ app.appIntent.toString()
										+ ", app.appIntent.toUri(0) = "
										+ app.appIntent.toUri(0));
						jsonObj.put("intent", app.appIntent.toUri(0));
						String base64 = Tools.bitmapToBase64(Tools
								.createIconBitmap(app.appIcon));
						jsonObj.put("bitmap", base64);
						// 把每个数据当作一对象添加到数组里
						jsonarray.put(jsonObj);// 向json数组里面添加对象

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Log.i(TAG,
								"######## bindWebFavoriteApp--- exception111 = "
										+ e.toString());
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					object.put("app", jsonarray);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.i(TAG, "######## bindWebFavoriteApp--- exception222 = "
							+ e.toString());
					e.printStackTrace();
				}

				sendJS("javascript:bindWebFavoriteApp" + "("
						+ object.toString() + ");");

				Log.i(TAG, "######## bindWebFavoriteApp done !!!!");
			}
		});

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
	 * 创建桌面快捷方式图标
	 * 
	 * @author hugo.ye
	 * @param context
	 * @param intent
	 * @param title
	 * @param icon
	 * */
	private void createSystemShortCut(Context context, Intent intent,
			String title, Bitmap icon) {
		Intent intent2 = new Intent();
		intent2.putExtra("app_title", title);
		intent2.putExtra("app_icon", icon);
		intent2.putExtra("app_intent", intent);
		intent2.setClassName(context.getPackageName(), "com.cooee.control.center.module.base.ShortcutService");
		context.startService(intent2);
	}

	public boolean startActivitySafely(Context context, Intent intent) {
		try {
			context.startActivity(intent);
			return true;
		} catch (ActivityNotFoundException e) {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.activity_not_found), Toast.LENGTH_SHORT)
					.show();
		}
		return false;
	}

	private boolean isPlayStoreInstalled(Context context) {
		String playPkgName = "com.android.vending";
		try {
			PackageInfo pckInfo = context.getPackageManager().getPackageInfo(
					playPkgName, PackageManager.GET_ACTIVITIES);
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(playPkgName, 0);
			boolean appStatus = appInfo.enabled;
			return appStatus;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void openGooglePlay(Context context, Intent intent) {
		if (isPlayStoreInstalled(context)) {
			intent.setClassName("com.android.vending",
					"com.android.vending.AssetBrowserActivity");
			startActivitySafely(context, intent);
		} else {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.google_play_not_install),
					Toast.LENGTH_LONG).show();
		}

	}
	private BroadcastReceiver mFavoriteReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "mFavoriteReceiver onReceive bindWebFavoriteApp");
			bindWebFavoriteApp();
		}
	};
}
